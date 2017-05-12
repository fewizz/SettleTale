package ru.settletale.client.gl;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

import ru.settletale.client.Window;
import ru.settletale.client.render.GlobalUniforms;
import ru.settletale.util.PrimitiveType;

public class GL {
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_ONLY_ERRORS = true;
	private static final UniformBuffer UBO_MATRICES = new UniformBuffer();
	private static final UniformBuffer UBO_MATRICES_INVERSED = new UniformBuffer();
	private static final UniformBuffer UBO_MATRIX_COMBINED = new UniformBuffer();
	private static final UniformBuffer UBO_DISPLAY_SIZE = new UniformBuffer();
	private static String previousMessage = "";
	private static Texture<?>[] activeTextures;
	private static int activeTextureIndex = 0;
	private static final HashIntObjMap<String> FROM_CODE_TO_ERROR_NAME_MAP = HashIntObjMaps.newMutableMap();
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static String vendor;
	public static int maxTextureUnitsAmount;
	public static final Matrix4fv PROJ_MATRIX = new Matrix4fv();
	private static final Matrix4fv PROJ_MATRIX_INVERSED = new Matrix4fv();
	public static final Matrix4fv VIEW_MATRIX = new Matrix4fv();
	private static final Matrix4fv VIEW_MATRIX_INVERSED = new Matrix4fv();
	private static final Matrix4fv MATRIX_COMBINED = new Matrix4fv();
	private static final VertexArray DEFAULT_VAO = new VertexArray();

	public static void init() {
		debug("GL init start");

		try (MemoryStack ms = MemoryStack.stackPush()) {
			UBO_MATRICES.gen().data(ms.callocFloat(32));
			UBO_MATRICES_INVERSED.gen().data(ms.callocFloat(32));
			UBO_MATRIX_COMBINED.gen().data(ms.callocFloat(16));
			UBO_DISPLAY_SIZE.gen().data(ms.callocFloat(2));
		}
		
		bindBufferBase(UBO_MATRICES, GlobalUniforms.MATRICES);
		bindBufferBase(UBO_MATRICES_INVERSED, GlobalUniforms.MATRICES_INVERSED);
		bindBufferBase(UBO_MATRIX_COMBINED, GlobalUniforms.MATRIX_COMBINED);
		bindBufferBase(UBO_DISPLAY_SIZE, GlobalUniforms.DISPLAY);

		DEFAULT_VAO.id = 0;
		vendor = GL11.glGetString(GL11.GL_VENDOR);
		versionMajor = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;
		maxTextureUnitsAmount = GL11.glGetInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
		activeTextures = new Texture<?>[maxTextureUnitsAmount];
		System.out.println("Vendor: " + GL.vendor);

		debug("GL init end");
	}

	public static void updateMatriciesUniformBlock() {
		debug("UpdateTransformUniformBlock start");

		PROJ_MATRIX.updateBackedBuffer();
		VIEW_MATRIX.updateBackedBuffer();
		UBO_MATRICES.offset(0).subData(PROJ_MATRIX.buffer);
		UBO_MATRICES.offset(16 * Float.BYTES).subData(VIEW_MATRIX.buffer);

		debug("UpdateTransformUniformBlock end");
	}
	
	public static void updateInversedMatricesUniformBlock() {
		PROJ_MATRIX.invert(PROJ_MATRIX_INVERSED);
		VIEW_MATRIX.invert(VIEW_MATRIX_INVERSED);
		
		PROJ_MATRIX_INVERSED.updateBackedBuffer();
		VIEW_MATRIX_INVERSED.updateBackedBuffer();
		UBO_MATRICES_INVERSED.offset(0).subData(PROJ_MATRIX_INVERSED.buffer);
		UBO_MATRICES_INVERSED.offset(16 * Float.BYTES).subData(VIEW_MATRIX_INVERSED.buffer);
	}
	
	public static void updateCombinedMatrixUniformBlock() {
		PROJ_MATRIX.mul(VIEW_MATRIX, MATRIX_COMBINED);
		
		MATRIX_COMBINED.updateBackedBuffer();
		UBO_MATRIX_COMBINED.subData(MATRIX_COMBINED.buffer);
	}

	public static void updateDisplaySizeUniformBlock() {
		debug("UpdateDisplaySizeUniformBlock start");

		try (MemoryStack ms = MemoryStack.stackPush()) {
			UBO_DISPLAY_SIZE.subData(ms.floats(Window.width, Window.height));
		}

		debug("UpdateDisplaySizeUniformBlock end");
	}

	public static void bindBufferBase(GLBuffer<?> buffer, int index) {
		GL30.glBindBufferBase(buffer.type, index, buffer.id);
	}

	public static void bindDefaultVAO() {
		DEFAULT_VAO.bind();
	}

	public static void bindTextureUnit(int index, Texture<?> texture) {
		if (activeTextureIndex != index) {
			setActiveTextureUnitIndex(index);
		}
		texture.bind();
		//setActiveTextureUnitTexture(texture);
	}
	
	static void onTextureDeleted(Texture<?> texture) {
		for(int i = 0; i < activeTextures.length; i++) {
			if(activeTextures[i] == texture) {
				activeTextures[i] = null;
			}
		}
	}

	public static void setActiveTextureUnitIndex(int index) {
		//activeTextures[index] = activeTextures[activeTextureIndex];
		activeTextureIndex = index;
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + activeTextureIndex);
	}

	public static void setActiveTextureUnitTexture(Texture<?> tex) {
		if (activeTextures[activeTextureIndex] == tex) {
			return;
		}
	
		activeTextures[activeTextureIndex] = tex;
		tex.bind();
	}

	public static void debug(String s) {
		debug(s, false);
	}

	public static void debug(String s, boolean printParent) {
		if (DEBUG) {
			int errorHex = GL11.glGetError();
			String errorName = null;

			if (DEBUG_ONLY_ERRORS && errorHex != 0) {
				errorName = getErrorNameFromHex(errorHex);

				throw new Error("OpenGL Error 0x" + Integer.toHexString(errorHex) + " \"" + errorName + "\"" + ": " + s + (printParent ? " | Previous: " + previousMessage : ""));
			}

			previousMessage = s;
		}
	}
	
	public static String getErrorNameFromHex(int hex) {
		String errorName = FROM_CODE_TO_ERROR_NAME_MAP.get(hex);
		
		if (errorName == null) {
			try {
				for (Field f : GL11.class.getDeclaredFields()) {
					if (hex != f.getInt(null)) {
						continue;
					}

					errorName = f.getName();
					FROM_CODE_TO_ERROR_NAME_MAP.put(hex, errorName);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return errorName;
	}

	public static int getGLPrimitiveType(PrimitiveType p) {
		switch (p) {
		case FLOAT:
			return GL11.GL_FLOAT;
		case BYTE:
			return GL11.GL_BYTE;
		case UBYTE:
			return GL11.GL_UNSIGNED_BYTE;
		case INT:
			return GL11.GL_INT;
		default:
			throw new Error("Undefined type");
		}
	}
}

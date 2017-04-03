package ru.settletale.client.gl;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

import ru.settletale.client.Window;
import ru.settletale.client.render.GlobalUniforms;
import ru.settletale.util.PrimitiveType;

public class GL {
	public static final boolean DEBUG = false;
	private static final boolean DEBUG_ONLY_ERRORS = true;
	private static final UniformBufferObject UBO_MATRICES = new UniformBufferObject();
	private static final UniformBufferObject UBO_DISPLAY_SIZE = new UniformBufferObject();
	private static String previousMessage = "";
	private static final Texture<?>[] ACTIVE_TEXTURES = new Texture<?>[256];
	private static int activeTextureIndex = 0;
	private static final HashIntObjMap<String> FROM_CODE_TO_ERROR_NAME_MAP = HashIntObjMaps.newMutableMap();
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static String vendor;
	public static final Matrix4fv PROJ_MATRIX = new Matrix4fv();
	public static final Matrix4fv VIEW_MATRIX = new Matrix4fv();
	private static final VertexArrayObject defaultVao = new VertexArrayObject();

	public static void init() {
		debug("GL init start");

		try (MemoryStack ms = MemoryStack.stackPush()) {
			UBO_MATRICES.gen().loadData(ms.callocFloat(32));
			UBO_DISPLAY_SIZE.gen().loadData(ms.callocFloat(2));
		}

		defaultVao.id = 0;
		vendor = GL11.glGetString(GL11.GL_VENDOR);
		versionMajor = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;
		System.out.println("Vendor: " + GL.vendor);

		debug("GL init end");
	}

	public static void updateTransformUniformBlock() {
		debug("UpdateTransformUniformBlock start");

		PROJ_MATRIX.updateBackedBuffer();
		VIEW_MATRIX.updateBackedBuffer();
		UBO_MATRICES.offset(0).loadSubData(PROJ_MATRIX.buffer);
		UBO_MATRICES.offset(16 * Float.BYTES).loadSubData(VIEW_MATRIX.buffer);

		bindBufferBase(UBO_MATRICES, GlobalUniforms.MATRICES);

		debug("UpdateTransformUniformBlock end");
	}

	public static void updateDisplaySizeUniformBlock() {
		debug("UpdateDisplaySizeUniformBlock start");

		try (MemoryStack ms = MemoryStack.stackPush()) {
			UBO_DISPLAY_SIZE.loadSubData(ms.floats(Window.width, Window.height));
			bindBufferBase(UBO_DISPLAY_SIZE, GlobalUniforms.DISPLAY);
		}

		debug("UpdateDisplaySizeUniformBlock end");
	}

	public static void bindBufferBase(BufferObject<?> buffer, int index) {
		GL30.glBindBufferBase(buffer.type, index, buffer.id);
	}

	public static void bindDefaultVAO() {
		defaultVao.bind();
	}

	public static void setActiveTexture(int index, Texture<?> texture) {
		if (activeTextureIndex != index) {
			setActiveTexture(index);
		}

		setActiveTextureUnitTexture(texture);
	}
	
	static void onTextureDeleted(Texture<?> texture) {
		for(int i = 0; i < ACTIVE_TEXTURES.length; i++) {
			if(ACTIVE_TEXTURES[i] == texture) {
				ACTIVE_TEXTURES[i] = null;
			}
		}
	}

	public static void setActiveTexture(int index) {
		activeTextureIndex = index;
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + activeTextureIndex);
	}

	public static void setActiveTextureUnitTexture(Texture<?> tex) {
		if (ACTIVE_TEXTURES[activeTextureIndex] == tex) {
			return;
		}
	
		ACTIVE_TEXTURES[activeTextureIndex] = tex;
		tex.bind();
	}

	public static void debug(String s) {
		debug(s, false);
	}

	public static void debug(String s, boolean printParent) {
		if (DEBUG) {
			int error = GL11.glGetError();

			if (DEBUG_ONLY_ERRORS && error != 0) {
				String errorName = FROM_CODE_TO_ERROR_NAME_MAP.get(error);

				if (errorName == null) {
					try {
						for (Field f : GL11.class.getDeclaredFields()) {
							if (error != f.getInt(null)) {
								continue;
							}

							errorName = f.getName();
							FROM_CODE_TO_ERROR_NAME_MAP.put(error, errorName);
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}

				throw new Error("OpenGL Error 0x" + Integer.toHexString(error) + " \"" + errorName + "\"" + ": " + s + (printParent ? " | Previous: " + previousMessage : ""));
			}

			previousMessage = s;
		}
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

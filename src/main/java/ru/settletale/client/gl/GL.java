package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import ru.settletale.client.Display;

public class GL {
	private static final boolean DEBUG = true;
	private static final boolean DEBUG_ONLY_ERRORS = true;
	private static UniformBufferObject uboMatricies;
	private static UniformBufferObject uboDisplaySize;
	private static String previousMessage = "";
	private static TextureAbstract<?>[] activeTextures = new TextureAbstract<?>[256];
	private static int activeTextureIndex = 0;
	
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static Matrix4fv projMatrix;
	public static Matrix4fv viewMatrix;

	public static void init() {
		debug("Init start");

		projMatrix = new Matrix4fv();
		viewMatrix = new Matrix4fv();

		try (MemoryStack ms = MemoryStack.stackPush()) {
			uboMatricies = new UniformBufferObject().gen().buffer(ms.callocFloat(32)).loadData();
			uboDisplaySize = new UniformBufferObject().gen().buffer(ms.callocFloat(2)).loadData();
		}

		versionMajor = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;

		debug("Init end");
	}

	public static void updateTransformUniformBlock() {
		debug("UpdateTransformUniformBlock start");

		projMatrix.updateBuffer();
		viewMatrix.updateBuffer();
		uboMatricies.buffer(projMatrix.buffer).offset(0).loadSubData();
		uboMatricies.buffer(viewMatrix.buffer).offset(16 * Float.BYTES).loadSubData();

		bindBufferBase(uboMatricies, 0);

		debug("UpdateTransformUniformBlock end");
	}

	public static void updateDisplaySizeUniformBlock() {
		debug("UpdateDisplaySizeUniformBlock start");

		try (MemoryStack ms = MemoryStack.stackPush()) {
			uboDisplaySize.buffer(ms.floats(Display.width, Display.height)).loadSubData();
			bindBufferBase(uboDisplaySize, 1);
		}

		debug("UpdateDisplaySizeUniformBlock end");
	}

	public static void bindBufferBase(BufferObject<?> buffer, int index) {
		GL30.glBindBufferBase(buffer.type, index, buffer.id);
	}

	public static void bindDefaultVAO() {
		GL30.glBindVertexArray(0);
	}

	public static void debug(String s) {
		debug(s, false);
	}

	public static void activeTexture(int index, TextureAbstract<?> texture) {
		if(activeTextureIndex != index)
			activeTextureUnit(index);
		activeTextureUnitTexture(texture);
	}
	
	public static void activeTextureUnit(int index) {
		activeTextureIndex = index;
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + activeTextureIndex);
	}
	
	public static void activeTextureUnitTexture(TextureAbstract<?> tex) {
		if(activeTextures[activeTextureIndex] == tex) {
			return;
		}
		
		activeTextures[activeTextureIndex] = tex;
		tex.bind();
	}


	public static void debug(String s, boolean printParent) {
		if (DEBUG) {
			int error = GL11.glGetError();
			if (DEBUG_ONLY_ERRORS && error != 0) {
				System.out.println("OpenGL: " + error + " " + s + (printParent ? " | Previous: " + previousMessage : ""));
			}
			previousMessage = s;
		}
	}
}

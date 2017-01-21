package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import ru.settletale.client.Display;

public class GL {
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_ONLY_ERRORS = true;
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static UniformBufferObject uboMatricies;
	public static UniformBufferObject uboDisplaySize;
	public static Matrix4fv projMatrix;
	public static Matrix4fv viewMatrix;

	public static void init() {
		debug("Init start");
		
		projMatrix = new Matrix4fv();
		viewMatrix = new Matrix4fv();
		
		try(MemoryStack ms = MemoryStack.stackPush()) {
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
	
	static String parent = "";
	public static void debug(String s, boolean printParent) {
		if (DEBUG) {
			int error = GL11.glGetError();
			if (DEBUG_ONLY_ERRORS && error != 0) {
				System.out.println("OpenGL: " + error + " " + s + (printParent ? " | Parent: " + parent : ""));
			}
			parent = s;
		}
	}
}

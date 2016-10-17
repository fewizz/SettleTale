package ru.settletale.client.opengl;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

public class OpenGL {
	public static final boolean debug = true;
	public static final boolean debugOnlyFails = true;
	public static int version;
	public static int versionMajor;
	public static int versionMinor;

	public static UniformBufferObject uboMatricies;
	public static Matrix4fv projMatrix;
	public static Matrix4fv viewMatrix;

	public static void init() {
		debug("Init start");
		projMatrix = new Matrix4fv();
		viewMatrix = new Matrix4fv();
		uboMatricies = new UniformBufferObject().gen();
		uboMatricies.bind();
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, BufferUtils.createFloatBuffer(32), GL15.GL_DYNAMIC_DRAW);
		uboMatricies.unbind();

		versionMajor = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;
		debug("Init end");
	}

	public static void updateGLProjMatrix() {
		debug("ProjMat gl start");
		int prevMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadMatrixf(projMatrix.get());

		if (prevMatrixMode != GL11.GL_PROJECTION) {
			GL11.glMatrixMode(prevMatrixMode);
		}
		debug("ProjMat gl end");
	}
	
	public static void updateProjMatrix() {
		GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projMatrix.buffer);
	}
	
	public static void updateViewMatrix() {
		GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, viewMatrix.buffer);
	}

	public static void updateGLViewMatrix() {
		debug("ViewMat gl start");
		int prevMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadMatrixf(viewMatrix.get());

		if (prevMatrixMode != GL11.GL_MODELVIEW) {
			GL11.glMatrixMode(prevMatrixMode);
		}
		debug("ProjMat gl end");
	}
	
	public static void updateTransformUniformBlock() {
		debug("UpdateTransformUniformBlock start");
		FloatBuffer buff = BufferUtils.createFloatBuffer(32);
		for (int i = 0; i < 16; i++) {
			buff.put(i, projMatrix.buffer.get(i));
		}
		if (viewMatrix.buffer != null) {
			for (int i = 0; i < 16; i++) {
				buff.put(i + 16, viewMatrix.buffer.get(i));
			}
		}
		uboMatricies.bind();
		GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, buff);
		uboMatricies.unbind();
		debug("UpdateTransformUniformBlock end");
	}
	
	public static void debug(String s) {
		if(debug) {
			int error = GL11.glGetError();
			if(debugOnlyFails && error == 0) {
				return;
			}
			System.out.println("OpenGL: " + GL11.glGetError() + " " + s);
		}
	}
}

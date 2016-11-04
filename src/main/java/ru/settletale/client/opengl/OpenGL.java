package ru.settletale.client.opengl;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

public class OpenGL {
	public static final boolean debug = false;
	public static final boolean debugOnlyFails = true;
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static UniformBufferObject uboMatricies;
	public static UniformBufferObject uboDisplaySize;
	public static Matrix4fv projMatrix;
	public static Matrix4fv viewMatrix;
	public static FloatBuffer unifromMatricies;
	public static FloatBuffer uniformDisplaySize;

	public static void init() {
		debug("Init start");
		projMatrix = new Matrix4fv();
		viewMatrix = new Matrix4fv();
		unifromMatricies = BufferUtils.createFloatBuffer(32);
		uniformDisplaySize = BufferUtils.createFloatBuffer(2);
		uboMatricies = new UniformBufferObject().gen();
		uboMatricies.bind();
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, BufferUtils.createFloatBuffer(32), GL15.GL_DYNAMIC_DRAW);
		uboMatricies.unbind();
		
		uboDisplaySize = new UniformBufferObject().gen();
		uboDisplaySize.bind();
		GL15.glBufferData(GL31.GL_UNIFORM_BUFFER, BufferUtils.createFloatBuffer(2), GL15.GL_DYNAMIC_DRAW);
		uboDisplaySize.unbind();

		versionMajor = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;
		debug("Init end");
	}

	@Deprecated
	public static void updateProjMatrix() {
		GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projMatrix.buffer);
	}
	
	@Deprecated
	public static void updateViewMatrix() {
		GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, viewMatrix.buffer);
	}
	
	@Deprecated
	public static void updateGLProjMatrix() {
		debug("ProjMat gl start");
		int prevMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		projMatrix.updateBuffer();
		GL11.glLoadMatrixf(projMatrix.buffer);

		if (prevMatrixMode != GL11.GL_PROJECTION) {
			GL11.glMatrixMode(prevMatrixMode);
		}
		debug("ProjMat gl end");
	}

	@Deprecated
	public static void updateGLViewMatrix() {
		debug("ViewMat gl start");
		int prevMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		viewMatrix.updateBuffer();
		GL11.glLoadMatrixf(viewMatrix.buffer);

		if (prevMatrixMode != GL11.GL_MODELVIEW) {
			GL11.glMatrixMode(prevMatrixMode);
		}
		debug("ProjMat gl end");
	}
	
	public static void updateTransformUniformBlock() {
		debug("UpdateTransformUniformBlock start");
		
		unifromMatricies.clear();
		projMatrix.updateBuffer();
		viewMatrix.updateBuffer();
		
		for (int i = 0; i < 16; i++) {
			unifromMatricies.put(i, projMatrix.buffer.get(i));
		}
		if (viewMatrix.buffer != null) {
			for (int i = 0; i < 16; i++) {
				unifromMatricies.put(i + 16, viewMatrix.buffer.get(i));
			}
		}
		uboMatricies.bind();
		GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, unifromMatricies);
		uboMatricies.unbind();
		
		debug("UpdateTransformUniformBlock end");
	}
	
	public static void updateDisplaySizeUniformBlock() {
		debug("UpdateDisplaySizeUniformBlock start");
		
		uboDisplaySize.bind();
		GL15.glBufferSubData(GL31.GL_UNIFORM_BUFFER, 0, uniformDisplaySize);
		uboDisplaySize.unbind();
		
		debug("UpdateDisplaySizeUniformBlock end");
	}
	
	public static void debug(String s) {
		if(debug) {
			int error = GL11.glGetError();
			if(debugOnlyFails && error == 0) {
				return;
			}
			System.out.println("OpenGL: " + error + " " + s);
		}
	}
}

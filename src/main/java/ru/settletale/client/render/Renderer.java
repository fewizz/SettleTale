package ru.settletale.client.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glGetError;

import org.lwjgl.system.MemoryStack;

import ru.settletale.client.Client;
import ru.settletale.client.render.util.GLUtils;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.util.Matrix4fs;
import ru.settletale.util.TickTimer;
import wrap.gl.GL;
import wrap.gl.UniformBuffer;
import wrap.gl.GLBuffer.BufferUsage;

public class Renderer {
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_GL = true;
	public static final boolean SHOW_ALL_MESSAEGES = false;
	private static final Matrix4fs VIEW_MATRIX_INVERSED = new Matrix4fs();
	private static final Matrix4fs MATRIX_COMBINED = new Matrix4fs();
	private static final Matrix4fs PROJ_MATRIX_INVERSED = new Matrix4fs();
	private static final UniformBuffer UBO_MATRICES = new UniformBuffer().usage(BufferUsage.DYNAMIC_DRAW);
	private static final UniformBuffer UBO_MATRICES_INVERSED = new UniformBuffer().usage(BufferUsage.DYNAMIC_DRAW);
	private static final UniformBuffer UBO_MATRIX_COMBINED = new UniformBuffer().usage(BufferUsage.DYNAMIC_DRAW);
	private static final UniformBuffer UBO_DISPLAY_SIZE = new UniformBuffer().usage(BufferUsage.DYNAMIC_DRAW);
	public static final Matrix4fs PROJ_MATRIX = new Matrix4fs();
	public static final Matrix4fs VIEW_MATRIX = new Matrix4fs();
	private static String previousGLDebugMessage = "";
	
	public static final TickTimer FRAMERATE_TICKER = new TickTimer(100F);
	static int frames = 0;
	static long start = System.nanoTime();
	public static int lastFPS;
	
	public static void init() {
		try (MemoryStack ms = MemoryStack.stackPush()) {
			UBO_MATRICES.gen().data(ms.callocFloat(32));
			UBO_MATRICES_INVERSED.gen().data(ms.callocFloat(32));
			UBO_MATRIX_COMBINED.gen().data(ms.callocFloat(16));
			UBO_DISPLAY_SIZE.gen().data(ms.callocFloat(2));
		}

		GL.bindBufferBase(UBO_MATRICES, GlobalUniforms.MATRICES);
		GL.bindBufferBase(UBO_MATRICES_INVERSED, GlobalUniforms.MATRICES_INVERSED);
		GL.bindBufferBase(UBO_MATRIX_COMBINED, GlobalUniforms.MATRIX_COMBINED);
		GL.bindBufferBase(UBO_DISPLAY_SIZE, GlobalUniforms.DISPLAY);
		
		updateDisplaySizeUniformBlock();
	}

	public static void render() {
		FRAMERATE_TICKER.start();

		WorldRenderer.render();

		Client.WINDOW.swapBuffers();
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		frames++;

		long time = System.nanoTime();

		if (time - start >= 1_000_000_000L) {
			start = time;

			if (DEBUG)
				System.out.println("FPS: " + frames);

			lastFPS = frames;
			frames = 0;
		}

		//FRAMERATE_TICKER.waitAndEndTimer();
	}
	
	public static void waitTicker() {
		FRAMERATE_TICKER.waitAndRestart();
	}
	
	public static void updateMatriciesUniformBlock() {
		debugGL("UpdateTransformUniformBlock start");

		PROJ_MATRIX.updateBackedBuffer();
		VIEW_MATRIX.updateBackedBuffer();
		UBO_MATRICES.offset(0).subData(PROJ_MATRIX.buffer);
		UBO_MATRICES.offset(16 * Float.BYTES).subData(VIEW_MATRIX.buffer);

		debugGL("UpdateTransformUniformBlock end");
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
		debugGL("UpdateDisplaySizeUniformBlock start");

		try (MemoryStack ms = MemoryStack.stackPush()) {
			UBO_DISPLAY_SIZE.subData(ms.floats(Client.WINDOW.getWidth(), Client.WINDOW.getHeight()));
		}

		debugGL("UpdateDisplaySizeUniformBlock end");
	}
	
	public static void debugGL(String s) {
		debugGL(s, false);
	}

	public static void debugGL(String s, boolean printParent) {
		if (DEBUG_GL) {
			int errorHex = glGetError();

			if (errorHex != 0) {
				String errorName = GLUtils.getErrorNameFromHex(errorHex);

				throw new Error("OpenGL Error 0x" + Integer.toHexString(errorHex) + " \"" + errorName + "\"" + ": " + s + (printParent ? " | Previous: " + previousGLDebugMessage : ""));
			}
			if(SHOW_ALL_MESSAEGES) {
				System.out.println(s);
			}

			previousGLDebugMessage = s;
		}
	}
}

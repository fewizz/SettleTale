package ru.settletale.client.render;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glGetError;

import org.lwjgl.system.MemoryStack;

import ru.settletale.client.Window;
import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.UniformBuffer;
import ru.settletale.client.Camera;
import ru.settletale.client.KeyListener;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.util.Matrix4fs;
import ru.settletale.util.TickTimer;

public class Renderer {
	public static final boolean DEBUG = true;
	public static final boolean DEBUG_GL = true;
	public static final boolean SHOW_ALL_MESSAEGES = false;
	private static final Matrix4fs VIEW_MATRIX_INVERSED = new Matrix4fs();
	private static final Matrix4fs MATRIX_COMBINED = new Matrix4fs();
	private static final Matrix4fs PROJ_MATRIX_INVERSED = new Matrix4fs();
	private static final UniformBuffer UBO_MATRICES = new UniformBuffer();
	private static final UniformBuffer UBO_MATRICES_INVERSED = new UniformBuffer();
	private static final UniformBuffer UBO_MATRIX_COMBINED = new UniformBuffer();
	private static final UniformBuffer UBO_DISPLAY_SIZE = new UniformBuffer();
	private static String previousGLDebugMessage = "";
	
	public static final TickTimer TIMER = new TickTimer(Window.frameRate);
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
	}

	public static void render() {
		TIMER.start();

		glfwPollEvents();
		glfwSetCursorPos(Window.id, Window.width / 2, Window.height / 2);

		KeyListener.updateForCurrentThread();
		Camera.update();

		WorldRenderer.render();

		glfwSwapBuffers(Window.id);

		frames++;

		long time = System.nanoTime();

		if (time - start > 1_000_000_000L) {
			start = time;

			if (DEBUG)
				System.out.println("FPS: " + frames);

			lastFPS = frames;
			frames = 0;
		}

		TIMER.waitAndEndTimer();
	}
	
	public static void updateMatriciesUniformBlock() {
		debugGL("UpdateTransformUniformBlock start");

		GL.PROJ_MATRIX.updateBackedBuffer();
		GL.VIEW_MATRIX.updateBackedBuffer();
		UBO_MATRICES.offset(0).subData(GL.PROJ_MATRIX.buffer);
		UBO_MATRICES.offset(16 * Float.BYTES).subData(GL.VIEW_MATRIX.buffer);

		debugGL("UpdateTransformUniformBlock end");
	}

	public static void updateInversedMatricesUniformBlock() {
		GL.PROJ_MATRIX.invert(PROJ_MATRIX_INVERSED);
		GL.VIEW_MATRIX.invert(VIEW_MATRIX_INVERSED);

		PROJ_MATRIX_INVERSED.updateBackedBuffer();
		VIEW_MATRIX_INVERSED.updateBackedBuffer();
		UBO_MATRICES_INVERSED.offset(0).subData(PROJ_MATRIX_INVERSED.buffer);
		UBO_MATRICES_INVERSED.offset(16 * Float.BYTES).subData(VIEW_MATRIX_INVERSED.buffer);
	}

	public static void updateCombinedMatrixUniformBlock() {
		GL.PROJ_MATRIX.mul(GL.VIEW_MATRIX, MATRIX_COMBINED);

		MATRIX_COMBINED.updateBackedBuffer();
		UBO_MATRIX_COMBINED.subData(MATRIX_COMBINED.buffer);
	}

	public static void updateDisplaySizeUniformBlock() {
		debugGL("UpdateDisplaySizeUniformBlock start");

		try (MemoryStack ms = MemoryStack.stackPush()) {
			UBO_DISPLAY_SIZE.subData(ms.floats(Window.width, Window.height));
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
				String errorName = GL.getErrorNameFromHex(errorHex);

				throw new Error("OpenGL Error 0x" + Integer.toHexString(errorHex) + " \"" + errorName + "\"" + ": " + s + (printParent ? " | Previous: " + previousGLDebugMessage : ""));
			}
			if(SHOW_ALL_MESSAEGES) {
				System.out.println(s);
			}

			previousGLDebugMessage = s;
		}
	}
}

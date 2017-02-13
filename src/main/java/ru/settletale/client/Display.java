package ru.settletale.client;

import static org.lwjgl.opengl.GL11.*;

import ru.settletale.client.gl.GL;
import ru.settletale.client.render.GLThread;

public class Display {
	public static float frameRate = 100F;
	public static int width = -1;
	public static int height = -1;
	public static long windowID;

	public static void onWindowResize(int w, int h) {
		if (w == Display.width && h == Display.height) {
			return;
		}
		Display.width = w;
		Display.height = h;

		GLThread.addTask(() -> {
			glViewport(0, 0, width, height);
			GL.projMatrix.identity();
			GL.projMatrix.perspective((float) Math.toRadians(120), (float) width / (float) height, 0.5F, 1000);
			GL.updateTransformUniformBlock();
			GL.updateDisplaySizeUniformBlock();
		});

	}
}

package ru.settletale.client;

import static org.lwjgl.opengl.GL11.*;

import ru.settletale.client.opengl.OpenGL;

public class Display {
	public static float frameRate = 60F;
	public static int width = -1;
	public static int height = -1;
	public static long window;

	public static void onWindowResize(int w, int h) {
		if (w == Display.width && h == Display.height) {
			return;
		}
		Display.width = w;
		Display.height = h;

		PlatformClient.runInRenderThread(new Runnable() {

			@Override
			public void run() {
				glViewport(0, 0, width, height);
				OpenGL.projMatrix.identity();
				OpenGL.projMatrix.perspective((float) Math.toRadians(95), (float) width / (float) height, 5, 1000);
				OpenGL.projMatrix.translate(0, 0, -100);
			}

		});

	}
}

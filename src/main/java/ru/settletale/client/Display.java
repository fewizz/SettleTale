package ru.settletale.client;

import static org.lwjgl.opengl.GL11.*;
import ru.settletale.client.opengl.GL;

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
				GL.projMatrix.identity();
				GL.projMatrix.perspective((float) Math.toRadians(120), (float) width / (float) height, 0.5F, 1000);
				GL.updateTransformUniformBlock();
				GL.updateDisplaySizeUniformBlock();
			}

		});

	}
}

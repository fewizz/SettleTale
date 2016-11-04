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
				OpenGL.projMatrix.perspective((float) Math.toRadians(120), (float) width / (float) height, 1, 1000);
				OpenGL.updateTransformUniformBlock();
				OpenGL.uniformDisplaySize.put(0, width);
				OpenGL.uniformDisplaySize.put(1, height);
				OpenGL.updateDisplaySizeUniformBlock();
			}

		});

	}
}

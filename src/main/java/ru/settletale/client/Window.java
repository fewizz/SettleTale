package ru.settletale.client;

import static org.lwjgl.opengl.GL11.*;

import ru.settletale.client.gl.GL;
import ru.settletale.client.render.GLThread;

public class Window {
	public static float frameRate = 100F;
	public static int width = -1;
	public static int height = -1;
	public static long id;

	public static void onWindowResize(int w, int h) {
		if (w == Window.width && h == Window.height) {
			return;
		}
		Window.width = w;
		Window.height = h;

		GLThread.addTask(() -> {
			glViewport(0, 0, width, height);
			GL.updateDisplaySizeUniformBlock();
		});

	}
}

package ru.settletale.client.render;

import static org.lwjgl.glfw.GLFW.*;

import ru.settletale.client.Window;
import ru.settletale.client.gl.GL;
import ru.settletale.client.Camera;
import ru.settletale.client.KeyListener;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.util.TickTimer;

public class MainRenderer {
	public static final TickTimer TIMER = new TickTimer(Window.frameRate);
	static int frames = 0;
	static long start = System.nanoTime();
	public static int lastFPS;

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

			if (GL.DEBUG)
				System.out.println("FPS: " + frames);

			lastFPS = frames;
			frames = 0;
		}

		TIMER.waitAndEndTimer();
	}
}

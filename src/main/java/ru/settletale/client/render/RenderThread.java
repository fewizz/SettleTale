package ru.settletale.client.render;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import ru.settletale.client.Display;
import ru.settletale.client.PlatformClient;
import ru.settletale.client.opengl.GL;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.util.TickTimer;

public class RenderThread extends Thread {
	private ConcurrentLinkedQueue<Runnable> runnableQueue;

	public RenderThread() {
		this.setName("Render thread");
		runnableQueue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void run() {
		init();
		renderLoop();
	}

	void renderLoop() {
		TickTimer timer = new TickTimer(PlatformClient.maxFPS);

		int frames = 0;
		long start = System.nanoTime();

		for (;;) {
			timer.start();

			callRunnables();
			WorldRenderer.render();
			glfwSwapBuffers(Display.window);

			timer.waitTimer();

			frames++;

			long end = System.nanoTime();
			if (end - start > 1_000_000_000L) {
				start += 1_000_000_000;

				System.out.println("time: " + start + ", FPS: " + frames);
				frames = 0;
			}
		}
	}

	private void callRunnables() {
		if (runnableQueue.size() == 0) {
			return;
		}
		for (Runnable run : runnableQueue) {
			run.run();
			runnableQueue.remove();
		}

	}

	void init() {
		glfwMakeContextCurrent(Display.window);
		org.lwjgl.opengl.GL.createCapabilities();
		glfwSwapInterval(0);
		GL.init();
		WorldRenderer.init();
	}

	public void run(Runnable runnable) {
		runnableQueue.add(runnable);
	}
}

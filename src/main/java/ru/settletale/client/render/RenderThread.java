package ru.settletale.client.render;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.opengl.GL;

import ru.settletale.client.Display;
import ru.settletale.client.opengl.OpenGL;
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
		TickTimer timer = new TickTimer(75);
		
		for (;;) {
			timer.start();
			
			callRunnables();
			WorldRenderer.render();
			glfwSwapBuffers(Display.window);
			
			timer.waitTimer();
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
		GL.createCapabilities();
		glfwSwapInterval(0);
		OpenGL.init();
		WorldRenderer.init();
	}

	public void run(Runnable runnable) {
		runnableQueue.add(runnable);
	}
}

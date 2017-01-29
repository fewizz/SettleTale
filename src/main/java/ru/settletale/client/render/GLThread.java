package ru.settletale.client.render;

import static org.lwjgl.glfw.GLFW.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import ru.settletale.client.Display;
import ru.settletale.client.opengl.GL;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.event.Event;
import ru.settletale.event.EventListener;
import ru.settletale.event.EventManager;
import ru.settletale.util.TickTimer;

public class GLThread extends Thread {
	static final private ConcurrentLinkedQueue<Runnable> TASK_QUEUE = new ConcurrentLinkedQueue<>();
	private static IRenderable renderable;

	public GLThread() {
		super("Render thread");
		EventManager.addEventListener(getClass());
	}
	
	@Override
	public void run() {
		initGL();
		GL.init();
		loop();
	}
	
	@EventListener(event = Event.ResourceManagerLoaded)
	static void onResourceManagerLoaded() {
		addTask(() -> {
			Drawer.init();
			WorldRenderer.init();
			
			renderable = WorldRenderer.INSTANCE;
		});
	}

	private static void loop() {
		TickTimer timer = new TickTimer(Display.frameRate);

		int frames = 0;
		long start = System.nanoTime();

		for (;;) {
			timer.start();

			doTasks();
			
			if(renderable != null) {
				renderable.render();
			}
			
			glfwSwapBuffers(Display.windowID);

			timer.waitTimer();

			frames++;
			
			if (System.nanoTime() - start > 1_000_000_000L) {
				start += 1_000_000_000;

				System.out.println("time: " + start + ", FPS: " + frames);
				frames = 0;
			}
		}
	}

	private static void doTasks() {
		Runnable r = TASK_QUEUE.poll();
		
		for(;;) {
			if(r == null) {
				return;
			}
			
			r.run();
			r = TASK_QUEUE.poll();
		}

	}

	public static synchronized void addTask(Runnable runnable) {
		TASK_QUEUE.add(runnable);
	}
	
	public static void setRenderable(IRenderable renderable) {
		GLThread.renderable = renderable;
	}
	
	static void initGL() {
		glfwMakeContextCurrent(Display.windowID);
		org.lwjgl.opengl.GL.createCapabilities();
		glfwSwapInterval(0);
	}
}

package ru.settletale.client.render;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import static org.lwjgl.glfw.GLFW.*;

import ru.settletale.client.Display;
import ru.settletale.client.gl.GL;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.event.Event;
import ru.settletale.event.EventListener;
import ru.settletale.event.EventManager;

public class GLThread extends Thread {
	private static final Queue<Runnable> TASK_QUEUE = new LinkedList<>();
	private static final Semaphore SEMAPHORE = new Semaphore(0);
	private static Stage stage = Stage.ONLY_DO_TASKS;

	public GLThread() {
		super("Render thread");
		EventManager.addEventListener(GLThread.class);
	}
	
	@Override
	public void run() {
		ru.settletale.client.GLFW.initGLFW();
		ru.settletale.client.LWJGL.initLWJGL();
		initGL();
		GL.init();
		mainLoop();
	}
	
	static void initGL() {
		glfwMakeContextCurrent(Display.windowID);
		org.lwjgl.opengl.GL.createCapabilities();
		glfwSwapInterval(0);
	}
	
	@EventListener(event = Event.RESOURCE_MANAGER_LOADED)
	static void onResourceManagerLoaded() {
		GLThread.addTask(() -> {
			Drawer.init();
			WorldRenderer.init();
			setStage(Stage.RENDER_WORLD);
		});
	}

	private static void mainLoop() {
		for(;;) {
			interrupted();
			stage.doStuff();
		}
	}

	private static void waitAndDoAvailableTask() throws InterruptedException {
		SEMAPHORE.acquire();
		TASK_QUEUE.poll().run();
	}
	
	public static void doAvailableTasks() {	
		for(;;) {
			if(SEMAPHORE.tryAcquire()) {
				TASK_QUEUE.poll().run();
			}
			else {
				return;
			}
		}
	}

	public static synchronized void addTask(Runnable runnable) {
		TASK_QUEUE.add(runnable);
		SEMAPHORE.release();
	}
	
	public static synchronized void setStage(Stage stage) {
		GLThread.stage = stage;
		currentThread().interrupt();
	}
	
	public enum Stage {
		ONLY_DO_TASKS {
			@Override
			public void doStuff() {
				try {
					waitAndDoAvailableTask();
				} catch (InterruptedException e) {
				}
			}
		},
		RENDER_WORLD {
			@Override
			public void doStuff() {
				MainRenderer.render();
			}
		};
		
		public void doStuff() {}
	}
}

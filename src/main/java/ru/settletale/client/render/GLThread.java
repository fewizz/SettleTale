package ru.settletale.client.render;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.CursorListener;
import ru.settletale.client.KeyListener;
import ru.settletale.client.MouseButtonListener;
import ru.settletale.client.Window;
import ru.settletale.client.WindowResizeListener;
import ru.settletale.client.gl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class GLThread extends Thread {
	private static final Queue<Runnable> TASK_QUEUE = new LinkedList<>();
	private static final Semaphore SEMAPHORE = new Semaphore(0);
	private static volatile Stage stage = Stage.ONLY_DO_TASKS;
	private static GLThread instance;

	public GLThread() {
		super("Render thread");
		instance = this;
	}
	
	@Override
	public void run() {
		initGLFW();
		initGL();
		GL.init();
		mainLoop();
	}
	
	public static void initGLFW() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		Window.windowID = glfwCreateWindow(1000, 600, "Settle Tale", MemoryUtil.NULL, MemoryUtil.NULL);
		Window.onWindowResize(1000, 600);
		
		if (Window.windowID == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(Window.windowID, KeyListener.INSTANCE);
		glfwSetMouseButtonCallback(Window.windowID, new MouseButtonListener());
		glfwSetFramebufferSizeCallback(Window.windowID, new WindowResizeListener());
		glfwSetCursorPosCallback(Window.windowID, new CursorListener());

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(Window.windowID, (vidmode.width() - Window.width) / 2, (vidmode.height() - Window.height) / 2);
		glfwShowWindow(Window.windowID);
	}
	
	static void initGL() {
		glfwMakeContextCurrent(Window.windowID);
		org.lwjgl.opengl.GL.createCapabilities();
		glfwSwapInterval(0);
	}

	private static void mainLoop() {
		for(;;) {
			if(interrupted()) {
				break;
			}
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
				Runnable r = TASK_QUEUE.poll();
				
				r.run();
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
		if(instance.getState() == Thread.State.WAITING) {
			instance.interrupt();
		}
	}
	
	public enum Stage {
		ONLY_DO_TASKS {
			@Override
			public void doStuff() {
				try {
					waitAndDoAvailableTask();
				} catch (InterruptedException e) {
					interrupted();
				}
			}
		},
		RENDER {
			@Override
			public void doStuff() {
				MainRenderer.render();
			}
		},
		STOP {
			@Override
			public void doStuff() {
				instance.interrupt();
				
				System.out.println("GLThread stopped!");
			}
		};
		
		public void doStuff() {}
	}
}

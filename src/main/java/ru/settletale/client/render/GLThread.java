package ru.settletale.client.render;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.CursorListener;
import ru.settletale.client.KeyListener;
import ru.settletale.client.MouseButtonListener;
import ru.settletale.client.Window;
import ru.settletale.client.WindowResizeListener;
import ru.settletale.client.gl.GL;
import ru.settletale.util.ThreadWithTasks;

import static org.lwjgl.glfw.GLFW.*;

public class GLThread extends ThreadWithTasks {
	public static final GLThread INSTANCE = new GLThread();

	public GLThread() {
		super(Stage.ONLY_DO_TASKS, "Render thread");
	}
	
	@Override
	public void init() {
		initGLFW();
		initGL();
		GL.init();
	}
	
	public static void addTask(Runnable runnable) {
		INSTANCE.addRunnableTask(runnable);
	}
	
	@Override
	public void doStuff() {
		doAvailableTasks();
		MainRenderer.render();
	}
	
	public static void initGLFW() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		Window.id = glfwCreateWindow(1000, 600, "Settle Tale", MemoryUtil.NULL, MemoryUtil.NULL);
		Window.onWindowResize(1000, 600);
		
		if (Window.id == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(Window.id, KeyListener.INSTANCE);
		glfwSetMouseButtonCallback(Window.id, new MouseButtonListener());
		glfwSetFramebufferSizeCallback(Window.id, new WindowResizeListener());
		glfwSetCursorPosCallback(Window.id, new CursorListener());

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(Window.id, (vidmode.width() - Window.width) / 2, (vidmode.height() - Window.height) / 2);
		glfwShowWindow(Window.id);
	}
	
	static void initGL() {
		glfwMakeContextCurrent(Window.id);
		org.lwjgl.opengl.GL.createCapabilities();
		glfwSwapInterval(0);
	}
}

package ru.settletale.client;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;

import ru.settletale.IPlatform;
import ru.settletale.client.render.RenderThread;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.client.resource.ResourceLoader;
import ru.settletale.util.Side;
import ru.settletale.util.TickTimer;
import ru.settletale.world.World;

public class PlatformClient implements IPlatform {
	public static int maxFPS = 75;
	private static RenderThread renderThread;
	World world;
	
	@Override
	public void start() {
		renderThread = new RenderThread();
		initLwjgl();
		initGlfw();
		ResourceLoader.init();
		System.gc();
		
		/** Creating world **/
		World world = new World();
		world.regionManager.listeners.add(WorldRenderer.INSTANCE);
		world.updateThread.start();
		/********************/
		
		renderThread.start(); /** Starting rendering **/
		
		clientUpdateLoop();
	}
	
	void clientUpdateLoop() {
		TickTimer timer = new TickTimer(100);
		
		for(;;) {
			timer.start();
			
			glfwPollEvents();
			KeyListener.update();
			Camera.update();
			
			timer.waitTimer();
		}
	}
	
	void initLwjgl() {
		System.setProperty("org.lwjgl.opengl.capabilities", "static");
		org.lwjgl.system.Library.initialize();
	}
	
	void initGlfw() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		Display.window = glfwCreateWindow(720, 480, "Settle Tale", MemoryUtil.NULL, MemoryUtil.NULL);
		Display.onWindowResize(720, 480);
		if (Display.window == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(Display.window, new KeyListener());
		glfwSetFramebufferSizeCallback(Display.window, new WindowResizeListener());

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(Display.window, (vidmode.width() - Display.width) / 2, (vidmode.height() - Display.height) / 2);
		glfwShowWindow(Display.window);
	}
	
	public static void runInRenderThread(Runnable runnable) {
		renderThread.run(runnable);
	}

	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

	@Override
	public World getWorld() {
		return world;
	}
	
	public static boolean isRenderThread() {
		return Thread.currentThread() == renderThread;
	}

}

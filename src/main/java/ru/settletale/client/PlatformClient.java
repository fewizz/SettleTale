package ru.settletale.client;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;

import ru.settletale.IPlatform;
import ru.settletale.client.render.RenderThread;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.util.Side;
import ru.settletale.util.TickTimer;
import ru.settletale.world.World;
import ru.settletale.world.region.RegionManagerOnePlayer;

public class PlatformClient implements IPlatform {
	public static float maxFPS = 100F;
	private static RenderThread renderThread;
	World world;
	
	@Override
	public void start() {
		renderThread = new RenderThread();
		initLwjgl();
		initGlfw();
		ResourceManager.init();
		System.gc();
		
		/** Creating world **/
		world = new World(new RegionManagerOnePlayer());
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

		Display.windowID = glfwCreateWindow(1000, 800, "Settle Tale", MemoryUtil.NULL, MemoryUtil.NULL);
		Display.onWindowResize(1000, 800);
		if (Display.windowID == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");
		
		glfwSetKeyCallback(Display.windowID, new KeyListener());
		glfwSetFramebufferSizeCallback(Display.windowID, new WindowResizeListener());

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(Display.windowID, (vidmode.width() - Display.width) / 2, (vidmode.height() - Display.height) / 2);
		glfwShowWindow(Display.windowID);
	}
	
	public static void runInRenderThread(Runnable runnable) {
		renderThread.addTask(runnable);
	}

	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

	@Override
	public World getWorld() {
		return world;
	}

}

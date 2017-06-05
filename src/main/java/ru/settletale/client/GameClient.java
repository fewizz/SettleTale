package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.GameAbstract;
import ru.settletale.client.render.Drawer;
import ru.settletale.client.render.Renderer;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.entity.EntityPlayer;
import ru.settletale.util.Side;
import ru.settletale.util.ThreadWithTasks;
import ru.settletale.world.World;
import ru.settletale.world.region.RegionManagerOnePlayer;

public class GameClient extends GameAbstract {
	public static EntityPlayer player;
	public static final ThreadWithTasks GL_THREAD = new ThreadWithTasks("OpengGL");
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
	
	@Override
	public void start() {
		LWJGL.init();
		
		/** Starting rendering **/
		GL_THREAD.start();
		GL_THREAD.addRunnableTask(() -> {
			initGLFW();
			initGL();
			ru.settletale.client.gl.GL.init();
			Renderer.init();
		});
		
		ResourceManager.loadResources();
		
		GL_THREAD.addRunnableTask(() -> {
			Drawer.init();
			WorldRenderer.init();
		});
		
		/** Creating world **/
		player = new EntityPlayer();
		world = new World(new RegionManagerOnePlayer());
		world.regionManager.listeners.add(WorldRenderer.INSTANCE);
		world.updateThread.start();
		/********************/
		
		GL_THREAD.addRunnableTask(() -> {
			for(;;) {
				GL_THREAD.doAvailableTasks();
				Renderer.render();
			}
		});
	}
	
	public static void initGLFW() {
		glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

		Window.id = glfwCreateWindow(1200, 600, "Settle Tale", MemoryUtil.NULL, MemoryUtil.NULL);
		Window.onWindowResize(1200, 600);
		
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

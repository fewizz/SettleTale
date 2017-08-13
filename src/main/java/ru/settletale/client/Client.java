package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

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
import wrap.gl.GL;
import wrap.glfw.GLFW;
import wrap.glfw.Window;

public class Client extends GameAbstract {
	public static final Window WINDOW = new Window();
	public static final ThreadWithTasks GL_THREAD = new ThreadWithTasks("OpengGL");
	public static EntityPlayer player;
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
	
	@Override
	public void start() {
		LWJGL.init();
		
		/** Starting rendering **/
		GL_THREAD.start();
		
		GL_THREAD.addTask(() -> {
			GLFW.init();
			
			WINDOW.create(1200, 600, "SettleTale");
			
			WINDOW.setKeyCallback(KeyListener.INSTANCE);
			WINDOW.setMouseButtonCallback(new MouseButtonListener());
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			WINDOW.setPos((vidmode.width() - WINDOW.getWidth()) / 2, (vidmode.height() - WINDOW.getHeight()) / 2);
			WINDOW.makeContextCurrent();
			WINDOW.swapInterval(0);
			
			GL.init();
			
			WINDOW.show();
			
			WINDOW.setFramebufferSizeCallback(new WindowResizeListener());
			GL11.glViewport(0, 0, WINDOW.getWidth(), WINDOW.getHeight());
		});
		
		ResourceManager.scanResourceFiles();
		
		GL_THREAD.addTask(() -> {
			Renderer.init();
			Drawer.init();
			WorldRenderer.init();
		});
		
		/** Creating world **/
		player = new EntityPlayer();
		world = new World(new RegionManagerOnePlayer());
		world.regionManager.listeners.add(WorldRenderer.INSTANCE);
		world.updateThread.start();
		/********************/
		
		for(;;) {
			GL_THREAD.addTask(() -> {
				GLFW.pollEvents();

				KeyListener.updateForCurrentThread();
				Camera.update();
				WINDOW.setCursorPos(Client.WINDOW.getWidth() / 2D, Client.WINDOW.getHeight() / 2D);
					
				Renderer.render();
			}).await();
			
			if(!GL_THREAD.isAlive()) {
				break;
			}
			
			Renderer.waitTicker();
		}
	}
}

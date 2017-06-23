package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWVidMode;

import ru.settletale.GameAbstract;
import ru.settletale.client.gl.GL;
import ru.settletale.client.glfw.GLFW;
import ru.settletale.client.glfw.Window;
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
		GL_THREAD.execute(() -> {
			GLFW.init();
			
			WINDOW.create(1200, 600, "SettleTale");
			
			WINDOW.setKeyCallback(KeyListener.INSTANCE);
			WINDOW.setMouseButtonCallback(new MouseButtonListener());
			WINDOW.setCursorPosCallback(new CursorListener());
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			WINDOW.setPos((vidmode.width() - WINDOW.getWidth()) / 2, (vidmode.height() - WINDOW.getHeight()) / 2);
			WINDOW.makeContextCurrent();
			WINDOW.swapInterval(0);
			
			WINDOW.show();
			
			GL.init();
			Renderer.init();
			
			WINDOW.setFramebufferSizeCallback(new WindowResizeListener());
		});
		
		ResourceManager.loadResources();
		
		GL_THREAD.execute(() -> {
			Drawer.init();
			WorldRenderer.init();
		});
		
		/** Creating world **/
		player = new EntityPlayer();
		world = new World(new RegionManagerOnePlayer());
		world.regionManager.listeners.add(WorldRenderer.INSTANCE);
		world.updateThread.start();
		/********************/
		
		GL_THREAD.execute(() -> {
			for(;;) {
				GL_THREAD.doAvailableTasks();
				
				GLFW.pollEvents();
				GameClient.WINDOW.setCursorPos(GameClient.WINDOW.getWidth() / 2, GameClient.WINDOW.getHeight() / 2);

				KeyListener.updateForCurrentThread();
				Camera.update();
				
				Renderer.render();
			}
		});
	}
}

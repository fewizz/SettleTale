package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.glfw.GLFW;
import ru.settletale.client.glfw.Window;
import ru.settletale.client.render.Drawer;
import ru.settletale.client.render.Renderer;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.entity.EntityPlayer;
import ru.settletale.registry.Registry;
import ru.settletale.util.ThreadWithTasks;
import ru.settletale.world.World;

public class Client {
	public static World world;
	public static final Window WINDOW = new Window();
	public static final ThreadWithTasks GL_THREAD = new ThreadWithTasks("OpengGL");
	public static EntityPlayer player;

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
			GL11.glViewport(0, 0, WINDOW.getWidth(), WINDOW.getHeight());
			
			WINDOW.show();
			WINDOW.setFramebufferSizeCallback(new WindowResizeListener());
		}).await();

		ResourceManager.scanResourceFiles();

		GL_THREAD.addTask(() -> {
			Renderer.init();
			Drawer.init();
			WorldRenderer.init();
		});

		/** Creating world **/
		Registry.init();
		world = new World();
		player = new EntityPlayer(world);
		world.chunkManager.listeners.add(WorldRenderer.INSTANCE);
		world.start();
		/********************/

		if (GL_THREAD.isHaveTasks())
			GL_THREAD.lastTask().await();

		GL_THREAD.setBehavior(thread -> {
			while (!thread.isInterrupted()) {
				GLFW.pollEvents();

				KeyListener.updateForCurrentThread();
				Camera.update();
				WINDOW.setCursorPos(WINDOW.getWidth() / 2D, WINDOW.getHeight() / 2D);

				Renderer.render();

				thread.executeAvailableTasks();
				
				for (;;) {
					long waitTime = Renderer.FRAMERATE_TICK_TIMER.getWaitTimeNano();
					if (waitTime <= 0)
						break;

					try {
						thread.waitAndExecuteTask(waitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
				
				Renderer.FRAMERATE_TICK_TIMER.waitAndRestart();
			}
		});
	}
}

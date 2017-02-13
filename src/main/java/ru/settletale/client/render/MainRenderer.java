package ru.settletale.client.render;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

import org.lwjgl.glfw.GLFW;

import ru.settletale.client.Camera;
import ru.settletale.client.Display;
import ru.settletale.client.KeyListener;
import ru.settletale.client.PlatformClient;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.util.TickTimer;

public class MainRenderer {
	static final TickTimer TIMER = new TickTimer(Display.frameRate);
	static int frames = 0;
	static long start = System.nanoTime();
	
	public static void render() {
		TIMER.start();
		
		glfwPollEvents();
		KeyListener.update();
		Camera.update();
		GLFW.glfwSetCursorPos(Display.windowID, Display.width / 2, Display.height / 2);
		
		GLThread.doAvailableTasks();
		
		PlatformClient.player.update();
		
		WorldRenderer.render();
		
		glfwSwapBuffers(Display.windowID);
		TIMER.waitTimer();
		frames++;
		
		if (System.nanoTime() - start > 1_000_000_000L) {
			start += 1_000_000_000;

			System.out.println("time: " + start + ", FPS: " + frames);
			frames = 0;
		}
	}
}

package ru.settletale.client.render;

import static org.lwjgl.glfw.GLFW.*;

import ru.settletale.client.Window;
import ru.settletale.client.gl.GL;
import ru.settletale.client.Camera;
import ru.settletale.client.KeyListener;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.util.TickTimer;

public class MainRenderer {
	public static final TickTimer TIMER = new TickTimer(Window.frameRate);
	static int frames = 0;
	static long start = System.nanoTime();
	public static int lastFPSCount;
	
	public static void render() {
		TIMER.start();
		
		glfwPollEvents();
		glfwSetCursorPos(Window.windowID, Window.width / 2, Window.height / 2);
		
		GLThread.doAvailableTasks();
		
		KeyListener.update();
		Camera.update();
		
		WorldRenderer.render();
		
		glfwSwapBuffers(Window.windowID);
		
		frames++;
		
		if (System.nanoTime() - start > 1_000_000_000L) {
			start += 1_000_000_000;

			if(GL.DEBUG)
				System.out.println("FPS: " + frames);
			
			lastFPSCount = frames;
			frames = 0;
		}
		
		TIMER.waitAndEndTimer();
	}
}

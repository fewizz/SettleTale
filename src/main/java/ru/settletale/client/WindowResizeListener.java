package ru.settletale.client;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL11;

import ru.settletale.client.render.Renderer;

public class WindowResizeListener extends GLFWFramebufferSizeCallback {

	@Override
	public void invoke(long window, int width, int height) {
		Client.GL_THREAD.execute(() -> {
			System.out.println(width + " " + height);
			GL11.glViewport(0, 0, width, height);
			Renderer.updateDisplaySizeUniformBlock();
		});
	}
}

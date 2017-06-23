package ru.settletale.client;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL11;

import ru.settletale.client.render.Renderer;

public class WindowResizeListener extends GLFWFramebufferSizeCallback {

	@Override
	public void invoke(long window, int width, int height) {
		GameClient.GL_THREAD.execute(() -> {
			GL11.glViewport(0, 0, width, height);
			Renderer.updateDisplaySizeUniformBlock();
		});
	}
}

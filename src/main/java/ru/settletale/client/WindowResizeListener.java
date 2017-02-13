package ru.settletale.client;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

public class WindowResizeListener extends GLFWFramebufferSizeCallback {

	@Override
	public void invoke(long window, int width, int height) {
		Display.onWindowResize(width, height);
	}
}

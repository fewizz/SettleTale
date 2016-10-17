package ru.settletale.client;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;

class WindowResizeListener extends GLFWFramebufferSizeCallback {

	@Override
	public void invoke(long window, int width, int height) {
		Display.onWindowResize(width, height);
	}
}

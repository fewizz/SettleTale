package ru.settletale.client;

import org.lwjgl.glfw.GLFWMouseButtonCallback;

public class MouseButtonListener extends GLFWMouseButtonCallback {

	@Override
	public void invoke(long window, int button, int action, int mods) {
		KeyListener.INSTANCE.invoke(window, button, 0, action, mods);
	}

}

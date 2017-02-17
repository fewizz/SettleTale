package ru.settletale.client;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class CursorListener extends GLFWCursorPosCallback {
	public static double x;
	public static double y;
	
	@Override
	public void invoke(long window, double xpos, double ypos) {
		x = xpos - (Window.width / 2);
		y = ypos - (Window.height / 2);
	}
}

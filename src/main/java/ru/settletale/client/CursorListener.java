package ru.settletale.client;

import org.lwjgl.glfw.GLFWCursorPosCallback;

class CursorListener extends GLFWCursorPosCallback {
	public static double x;
	public static double y;
	
	@Override
	public void invoke(long window, double xpos, double ypos) {
		x = xpos - (Display.width / 2);
		y = ypos - (Display.height / 2);
	}
}

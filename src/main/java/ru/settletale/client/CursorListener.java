package ru.settletale.client;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class CursorListener extends GLFWCursorPosCallback {
	
	@Override
	public void invoke(long window, double xpos, double ypos) {
		Cursor.POSITION.x = (float) (xpos - (Window.width / 2));
		Cursor.POSITION.y = (float) ((Window.height - ypos) - (Window.height / 2));
	}
}

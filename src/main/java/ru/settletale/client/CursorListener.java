package ru.settletale.client;

import org.lwjgl.glfw.GLFWCursorPosCallback;

public class CursorListener extends GLFWCursorPosCallback {
	
	@Override
	public void invoke(long window, double xpos, double ypos) {
		Cursor.POSITION.x = (float) (xpos - (GameClient.WINDOW.getWidth() / 2));
		Cursor.POSITION.y = (float) ((GameClient.WINDOW.getHeight() - ypos) - (GameClient.WINDOW.getHeight() / 2));
	}
}

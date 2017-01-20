package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
	public static float x;
	public static float z;
	public static float y = 200;
	public static float rotationX = 90;
	public static float rotationY = 0;
	
	static void update() {
		if(KeyListener.isKeyPressed(GLFW_KEY_W)) {
			z -= Math.cos(Math.toRadians(rotationY));
			x += Math.sin(Math.toRadians(rotationY));
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_S)) {
			z += Math.cos(Math.toRadians(rotationY));
			x -= Math.sin(Math.toRadians(rotationY));
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_D)) {
			z += Math.sin(Math.toRadians(rotationY));
			x += Math.cos(Math.toRadians(rotationY));
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_A)) {
			z -= Math.sin(Math.toRadians(rotationY));
			x -= Math.cos(Math.toRadians(rotationY));
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
			y--;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
			y++;
		}
		
		rotationX += CursorListener.y;
		rotationY += CursorListener.x;
	}
}

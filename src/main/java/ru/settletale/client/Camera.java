package ru.settletale.client;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
	public static float x;
	public static float z;
	public static float aX = 45;
	public static float aY = 0;
	
	static void update() {
		if(KeyListener.isKeyPressed(GLFW_KEY_W)) {
			z -= Math.cos(Math.toRadians(aY));
			x += Math.sin(Math.toRadians(aY));//1F;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_S)) {
			z += Math.cos(Math.toRadians(aY));
			x -= Math.sin(Math.toRadians(aY));
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_D)) {
			z += Math.sin(Math.toRadians(aY));
			x += Math.cos(Math.toRadians(aY));
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_A)) {
			z -= Math.sin(Math.toRadians(aY));
			x -= Math.cos(Math.toRadians(aY));
		}
		
		if(KeyListener.isKeyPressed(GLFW_KEY_UP)) {
			aX += 1F;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
			aX -= 1F;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
			aY += 1F;
		}
		if(KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
			aY -= 1F;
		}
	}
}

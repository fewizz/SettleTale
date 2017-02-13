package ru.settletale.client;

import org.joml.Vector3f;

public class Camera {
	public static Vector3f position = new Vector3f();
	public static float rotationX = 90;
	public static float rotationY;

	public static void update() {
		position.set(PlatformClient.player.position);
		
		rotationX += CursorListener.y;
		rotationY += CursorListener.x;
		
		if(rotationX > 90) {
			rotationX = 90;
		}
		if(rotationX < -90) {
			rotationX = -90;
		}
	}
}

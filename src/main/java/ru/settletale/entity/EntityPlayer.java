package ru.settletale.entity;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.Vector3f;

import ru.settletale.client.Camera;
import ru.settletale.client.CursorListener;
import ru.settletale.client.KeyListener;

public class EntityPlayer extends Entity {

	@Override
	public void update() {
		position.updatePrevious();
		
		Vector3f add = new Vector3f(0);
		rotationX = Camera.rotationX;
		rotationY = Camera.rotationY;
		
		if (KeyListener.isKeyPressed(GLFW_KEY_W)) {
			add.z += (float) -Math.cos(Math.toRadians(rotationY));
			add.x += (float) Math.sin(Math.toRadians(rotationY));
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_S)) {
			add.z += (float) Math.cos(Math.toRadians(rotationY));
			add.x += (float) -Math.sin(Math.toRadians(rotationY));
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_D)) {
			add.z += (float) Math.sin(Math.toRadians(rotationY));
			add.x += (float) Math.cos(Math.toRadians(rotationY));
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_A)) {
			add.z += (float) -Math.sin(Math.toRadians(rotationY));
			add.x += (float) -Math.cos(Math.toRadians(rotationY));
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
			add.y += -1;
		}
		if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
			add.y += 1;
		}
		
		position.add(add);
	}

}
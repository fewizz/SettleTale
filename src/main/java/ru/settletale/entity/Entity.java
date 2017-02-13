package ru.settletale.entity;

import ru.settletale.util.Vector3fp;

public abstract class Entity {
	public Vector3fp position = new Vector3fp(0, 100, 0);
	public float rotationX = 0;
	public float rotationY = 0;
	
	public abstract void update();
}

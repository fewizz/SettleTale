package ru.settletale.entity;

import ru.settletale.util.Vector3dp;

public abstract class Entity {
	public Vector3dp position = new Vector3dp(0, 100, 0);
	public float rotationX = 0;
	public float rotationY = 0;
	
	public abstract void update();
}

package ru.settletale.phys;

import org.joml.Vector3f;

public abstract class CollidableObject {
	Vector3f position;
	AABB aabb;
	
	public void update() {
		
	}
	
	abstract void generateAABB();
	
	public boolean isStatic() {
		return false;
	}
}

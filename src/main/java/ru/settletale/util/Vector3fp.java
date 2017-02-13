package ru.settletale.util;

import org.joml.Vector3f;

/** Contains previous vector */
public class Vector3fp extends Vector3f {
	private Vector3f previous = new Vector3f();
	
	public Vector3fp(float x, float y, float z) {
		super(x, y, z);
	}
	
	public void updatePrevious() {
		previous.set(this);
	}
}
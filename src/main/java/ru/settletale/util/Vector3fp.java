package ru.settletale.util;

import org.joml.Vector3f;
import org.joml.Vector3fc;

/** Contains previous vector */
public class Vector3fp extends Vector3f {
	public Vector3f previous = new Vector3f();
	
	public Vector3fp(Vector3fc v) {
		super(v);
	}
	public Vector3fp(float x, float y, float z) {
		super(x, y, z);
	}
	
	public void updatePrevious() {
		previous.set(this);
	}
	
	public void set(Vector3fp v) {
		this.set((Vector3f)v);
		this.previous.set(v.previous);
	}
}
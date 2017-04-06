package ru.settletale.util;

import org.joml.Vector3d;
import org.joml.Vector3dc;

/** Contains previous vector */
public class Vector3dp extends Vector3d {
	public Vector3d previous = new Vector3d();
	
	public Vector3dp() {
		super();
	}
	
	public Vector3dp(Vector3dc v) {
		super(v);
	}
	
	public Vector3dp(Vector3dp v) {
		set(v);
	}
	
	public Vector3dp(double x, double y, double z) {
		super(x, y, z);
	}
	
	public void updatePrevious() {
		previous.set(this);
	}
	
	public void set(Vector3dp v) {
		super.set(v);
		this.previous.set(v.previous);
	}
}
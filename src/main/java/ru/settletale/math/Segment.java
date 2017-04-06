package ru.settletale.math;

import org.joml.Vector3d;

public class Segment {
	public final Vector3d p1 = new Vector3d();
	public final Vector3d p2 = new Vector3d();

	public Segment(Vector3d p1, Vector3d p2) {
		set(p1, p2);
	}

	public Segment() {
	}

	public void set(Vector3d p1, Vector3d p2) {
		this.p1.set(p1);
		this.p2.set(p2);
	}

	public double length() {
		double x = (p1.x - p2.x);
		double y = (p1.y - p2.y);
		double z = (p1.z - p2.z);
		return Distance.point(x, y, z);
	}
}

package ru.settletale.math;

import org.joml.Vector3f;

public class Line {
	Vector3f origin;
	Vector3f dir;

	public Line() {
	}
	
	public Line(Vector3f origin, Vector3f dir) {
		this.origin = origin;
		this.dir = dir;
	}
	
	public void initBy2Ponits(Vector3f p1, Vector3f p2) {
		this.origin = p1;
		this.dir = new Vector3f();
		p2.sub(p1, dir);
	}

	public float getYAtX(float x) {
		float xDist = x - origin.x;
		float steps = xDist / dir.x;
		return origin.y + (dir.y * steps);
	}
	
	public float getYAtZ(float z) {
		float zDist = z - origin.z;
		float steps = zDist / dir.z;
		return origin.y + (dir.y * steps);
	}
	
	public float getXAtY(float y) {
		float yDist = y - origin.y;
		float steps = yDist / dir.y;
		return origin.x + (dir.x * steps);
	}
	
	public float getXAtZ(float z) {
		float zDist = z - origin.z;
		float steps = zDist / dir.z;
		return origin.x + (dir.x * steps);
	}
	
	public static void main(String[] args) {
		Line l = new Line(new Vector3f(0), new Vector3f(1, 2, 1));
		System.out.println(l.getYAtX(5));
	}
}

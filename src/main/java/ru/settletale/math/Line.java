package ru.settletale.math;

import org.joml.Vector3d;

public class Line {
	final Vector3d origin = new Vector3d();
	final Vector3d dir = new Vector3d();

	public Line() {
	}
	
	public Line(Vector3d p1, Vector3d p2) {
		this.origin.set(p1);
		p2.sub(p1, dir);
	}

	public double getYAtX(double x) {
		double xDist = x - origin.x;
		double steps = xDist / dir.x;
		return origin.y + (dir.y * steps);
	}
	
	public double getYAtZ(double z) {
		double zDist = z - origin.z;
		double steps = zDist / dir.z;
		return origin.y + (dir.y * steps);
	}
	
	public double getXAtY(double y) {
		double yDist = y - origin.y;
		double steps = yDist / dir.y;
		return origin.x + (dir.x * steps);
	}
	
	public double getXAtZ(double z) {
		double zDist = z - origin.z;
		double steps = zDist / dir.z;
		return origin.x + (dir.x * steps);
	}
	
	/*public static void main(String[] args) {
		Line l = new Line(new Vector3f(0), new Vector3f(1, 2, 1));
		System.out.println(l.getYAtX(5));
	}*/
}

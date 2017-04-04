package ru.settletale.math;

import org.joml.Vector3f;

public class Intersection {

	public static Vector3f linePlane(Line line, Plane plane) {
		return linePlane(line, plane, new Vector3f());
	}
	
	public static Vector3f linePlane(Line line, Plane plane, Vector3f result) {
		Vector3f temp1 = new Vector3f();

		plane.origin.sub(line.origin, temp1);

		float numerator = temp1.dot(plane.normal);
		float denominator = line.dir.dot(plane.normal);
		
		if(denominator == 0) { // parallel
			return null;
		}

		float d = numerator / denominator;
		
		result.set(line.dir);
		result.mul(d);
		result.add(line.origin);
		return result;
	}
	
	public static void main(String[] args) {
		Line l = new Line(new Vector3f(0, 5, 0), new Vector3f(1, -1, 0));
		Plane p = new Plane(new Vector3f(0), new Vector3f(0, 1, 0));
		
		Vector3f in = linePlane(l, p);
		System.out.println(in);
	}
}

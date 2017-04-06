package ru.settletale.math;

public class Intersection {
	
	// From wiki
	public static void linePlane(Line line, Plane plane, IntersectionResult result) {
		result.succes = false;
		double denominator = line.dir.dot(plane.normal);
		if(denominator == 0) { // parallel
			return;
		}
		
		plane.origin.sub(line.origin, result);
		double numerator = result.dot(plane.normal);

		double d = numerator / denominator;
		
		result.set(line.dir);
		result.mul(d);
		result.add(line.origin);
		result.succes = true;
		return;
	}
	
	public static void segmentPlane(Segment segment, Plane plane, IntersectionResult result) {
		result.succes = false;
		segment.p2.sub(segment.p1, result); // dir
		double denominator = result.dot(plane.normal);
		if(denominator == 0) { // parallel
			return;
		}
		
		plane.origin.sub(segment.p1, result);
		double numerator = result.dot(plane.normal);

		double d = numerator / denominator;
		if(d < 0 || d > 1) { // bcos segment
			return;
		}
		
		segment.p2.sub(segment.p1, result); // dir
		result.mul(d);
		result.add(segment.p1);
		result.succes = true;
	}
}

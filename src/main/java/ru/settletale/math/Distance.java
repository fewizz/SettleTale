package ru.settletale.math;

import org.joml.Vector3d;

import ru.settletale.util.MathUtils;

public class Distance {
	public static double point(float x, float y, float z) {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double point(double x, double y, double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double pointPoint(float x1, float y1, float z1, float x2, float y2, float z2) {
		float x = x2 - x1;
		float y = y2 - y1;
		float z = z2 - z1;
		
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double pointPoint(double x1, double y1, double z1, double x2, double y2, double z2) {
		double x = x2 - x1;
		double y = y2 - y1;
		double z = z2 - z1;
		
		return Math.sqrt(x * x + y * y + z * z);
	}
	
	public static double segmentPoint(Segment segment, Point point) {
		return segmentPoint(segment, point.origin);
	}

	public static double segmentPoint(Segment segment, Vector3d vector) {
		return segmentPoint(segment, vector.x, vector.y, vector.z);
	}

	public static double segmentPoint(Segment segment, double pointx, double pointy, double pointz) {
		double ax = pointx - segment.p1.x; // a
		double ay = pointy - segment.p1.y;
		double az = pointz - segment.p1.z;

		double bx = segment.p2.x - segment.p1.x; // b
		double by = segment.p2.y - segment.p1.y;
		double bz = segment.p2.z - segment.p1.z;

		double alen = point(ax, ay, az);
		double blen = point(bx, by, bz);

		double cosalen = MathUtils.dot(ax, ay, az, bx, by, bz) / blen;
		
		if(cosalen == 0) {
			return 0;
		}
		if(cosalen < 0) {
			return alen;
		}
		if(cosalen >= blen) {
			return pointPoint(ax, ay, az, bx, by, bz);
		}
		
		double cos = cosalen / alen;
		double sin = Math.sqrt(1D - (cos * cos));

		return alen * sin;
	}
	
	public static void main(String[] args) {
		Segment s = new Segment(new Vector3d(), new Vector3d(10, 0, 0));
		
		double d = segmentPoint(s, 5, -10, 0);
		System.out.println(d);
	}
}

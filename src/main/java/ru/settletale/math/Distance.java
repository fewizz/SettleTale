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
		double px = pointx - segment.p1.x; // a
		double py = pointy - segment.p1.y;
		double pz = pointz - segment.p1.z;

		double p2x = segment.p2.x - segment.p1.x; // b
		double p2y = segment.p2.y - segment.p1.y;
		double p2z = segment.p2.z - segment.p1.z;

		double lenp = point(px, py, pz);
		double lenp2 = point(p2x, p2y, p2z);

		double cosLenp2 = MathUtils.dot(px, py, pz, p2x, p2y, p2z) / lenp;
		
		if(cosLenp2 <= 0) {
			return lenp;
		}
		if(cosLenp2 >= lenp2) {
			return pointPoint(px, py, pz, p2x, p2y, p2z);
		}
		
		double cos = cosLenp2 / lenp2;
		double sin = Math.sqrt(1D - (cos * cos));

		return (lenp * sin);
	}
}

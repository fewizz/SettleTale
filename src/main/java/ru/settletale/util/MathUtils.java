package ru.settletale.util;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class MathUtils {
	public static long clamp(int x, int z) {
		return ((long) z & 0xFFFFFFFFL) | ((long) x << 32);
	}

	public static int floor(float f) {
		if(f < 0) {
			return (int) (f) - 1;
		}
		
		return (int) f;
	}
	
	public static int floor(double f) {
		if(f < 0) {
			return (int) (f) - 1;
		}
		
		return (int) f;
	}

	public static int ceil(float f) {
		if (f > 0) {
			return (int) (f) + 1;
		}
		return (int) f;
	}
	
	public static int ceil(double f) {
		if (f > 0) {
			return (int) (f) + 1;
		}
		return (int) f;
	}

	public static float fract(float x) {
		return (float) (x - (float)floor(x));
	}

	public static float dot(float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}
	
	public static double dot(double x1, double y1, double z1, double x2, double y2, double z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}
	
	public static float dot(float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	public static float smoothstep(float edge0, float edge1, float x) {
		float t;
		t = clamp(1, 0, (x - edge0) / (edge1 - edge0));
		return t * t * (3 - 2 * t);
	}

	public static float clamp(float max, float min, float x) {
		return Math.max(min, Math.min(x, max));
	}
	
	public static Vector3f interpolate(Vector3f v1, Vector3f v2, Vector3f dest, float f) {
		dest.set(v2);
		dest.sub(v1);
		dest.mul(f);
		dest.add(v1);
		
		return dest;
	}
	
	public static Vector3d interpolate(Vector3d v1, Vector3d v2, Vector3d dest, double d) {
		dest.set(v2);
		dest.sub(v1);
		dest.mul(d);
		dest.add(v1);
		
		return dest;
	}
}

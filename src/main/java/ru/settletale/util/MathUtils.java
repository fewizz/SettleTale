package ru.settletale.util;

public class MathUtils {
	public static long clamp(int x, int z) {
		return ((long)z & 0xFFFFFFFFL) | ((long)x << 32);
	}
	
	public static int floor(float f) {
		return f < 0 ? (int)(f) - 1 : (int)f;
	}
	
	public static int floor2(float f) {
		if(f < 0) {
			if(f % 1 != 0) {
				return (int)(f) - 1;
			}
		}
		return (int)f;
	}
	
	public static int ceil(float f) {
		if(f > 0) {
			if(f % 1 != 0) {
				return (int)(f) + 1;
			}
		}
		return (int)f;
	}
	
	public static float fract(float x) {
		return (float) (x - Math.floor(x));
	}
	
	public static float dot(float x1, float y1, float x2, float y2) {
		return x1 * y1 + x2 * y2;
	}
	
	public static float smoothstep(float edge0, float edge1, float x) {
		float t;
	    t = clamp((x - edge0) / (edge1 - edge0), 0, 1);
	    return t * t * (3 - 2 * t);
	}
	
	public static float clamp(float x, float min, float max) {
		return Math.max(min, Math.min(x, max));
	}
}

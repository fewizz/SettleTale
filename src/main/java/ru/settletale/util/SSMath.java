package ru.settletale.util;

public class SSMath {
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
}

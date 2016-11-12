package ru.settletale.world;

import ru.settletale.util.OpenSimplexNoise;
import ru.settletale.util.SSMath;

public abstract class Layer {
	private byte[] values;
	private static final long RANDOM_NUMBER = 6546213132131313131L;
	public static OpenSimplexNoise noise = new OpenSimplexNoise(1337);
	Layer parent;
	long seed;
	
	public Layer(Layer parent) {
		this.parent = parent;
	}
	
	public abstract byte[] getValues(int x, int z, int width, int length);
	
	public byte[] getByteArray(int width, int length) {
		if(values == null || values.length != width * length) {
			values = new byte[width * length];
			return values;
		}
		
		return values;
	}
	
	public static boolean getPRandomBoolean(int x, int z) {
		return (getPRNumberBySeed(SSMath.clamp(x, z)) & 0x1) == 0;
	}
	
	/** All into one method. for speed =P **/
	public static int getPRInt(int x, int z, int border) {
		long number = ((long)z & 0xFFFFFFFFL) | ((long)x << 32);
		
		long i1 = (number & 0xFFFF) * 21354343;
		long i2 = ((number >>> 16) & 0xFFFF) * 3854351;
		long i3 = ((number >>> 32) & 0xFFFF) * 1635447;
		long i4 = ((number >>> 48) & 0xFFFF) * 3543531;
		
		long toReturn = (((i1 * number) * i1 * i1 * i1 * i1 * i1 * i1 * i1 * RANDOM_NUMBER) + ((i2 * number) * i2 * i2 * i2 * i2 * i2 * i2 * i2 * RANDOM_NUMBER) + (i3 * i3 * i3 * i3 * i3 * i3 * i3 * i3 * RANDOM_NUMBER) + ((i4 * number) * i4 * i4 * i4 * i4 * i4 * i4 * i4 * RANDOM_NUMBER) + (number * number * number * number * number * number * number * number * RANDOM_NUMBER)) / (123135431L + (i1 + i2 + i3 + i4 + number));
		
		int num = (int) ((double)toReturn % (double)border);
		return num < 0 ? -num : num;
	}
	
	public static int getPRInt(long number, int border) {
		int num = (int) ((double)getPRNumberBySeed(number) % (double)border);
		return num < 0 ? -num : num;
	}
	
	public static long getPRNumberBySeed(long number) {
		long i1 = (number & 0xFFFF) * 21354343;
		long i2 = ((number >>> 16) & 0xFFFF) * 3854351;
		long i3 = ((number >>> 32) & 0xFFFF) * 1635447;
		long i4 = ((number >>> 48) & 0xFFFF) * 3543531;
		
		long toReturn = (((i1 * number) * i1 * i1 * i1 * i1 * i1 * i1 * i1 * RANDOM_NUMBER) + ((i2 * number) * i2 * i2 * i2 * i2 * i2 * i2 * i2 * RANDOM_NUMBER) + (i3 * i3 * i3 * i3 * i3 * i3 * i3 * i3 * RANDOM_NUMBER) + ((i4 * number) * i4 * i4 * i4 * i4 * i4 * i4 * i4 * RANDOM_NUMBER) + (number * number * number * number * number * number * number * number * RANDOM_NUMBER)) / (123135431L + (i1 + i2 + i3 + i4 + number));
		return toReturn;
	}
}

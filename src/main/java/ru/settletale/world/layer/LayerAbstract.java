package ru.settletale.world.layer;

import ru.settletale.util.OpenSimplexNoise;
import ru.settletale.util.MathUtils;

public abstract class LayerAbstract {
	private byte[] values;
	private static final long RANDOM_NUMBER_CONST = 6557954324650566504L;
	public static long seed;
	public static final OpenSimplexNoise SIMPLEX_NOISE = new OpenSimplexNoise(1337);
	LayerAbstract parent;
	
	public LayerAbstract(LayerAbstract parent) {
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
		return (getPRNumberBySeed(MathUtils.clampLong(x, z)) & 0x1) == 0;
	}
	
	// All into one method. for speed =P
	public static int getPRInt(int x, int z, int border) {
		long number = ((long)z & 0xFFFFFFFFL) | ((long)x << 32);
		
		long i1 = (number & 0xFFFF) * 21354343;
		long i2 = ((number >>> 16) & 0xFFFF) * 3854351;
		long i3 = ((number >>> 32) & 0xFFFF) * 1635447;
		long i4 = ((number >>> 48) & 0xFFFF) * 3543531;
		
		long toReturn = (((i1 * number) * i1 * i1 * i1 * i1 * i1 * i1 * i1 * RANDOM_NUMBER_CONST * seed) + ((i2 * number) * i2 * i2 * i2 * i2 * i2 * i2 * i2 * RANDOM_NUMBER_CONST * seed) + (i3 * i3 * i3 * i3 * i3 * i3 * i3 * i3 * RANDOM_NUMBER_CONST * seed) + ((i4 * number) * i4 * i4 * i4 * i4 * i4 * i4 * i4 * RANDOM_NUMBER_CONST * seed) + (number * number * number * number * number * number * number * number * RANDOM_NUMBER_CONST * seed)) / (123135431L + (i1 + i2 + i3 + i4 + number));
		
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
		
		long toReturn = (((i1 * number) * i1 * i1 * i1 * i1 * i1 * i1 * i1 * RANDOM_NUMBER_CONST * seed) + ((i2 * number) * i2 * i2 * i2 * i2 * i2 * i2 * i2 * RANDOM_NUMBER_CONST * seed) + (i3 * i3 * i3 * i3 * i3 * i3 * i3 * i3 * RANDOM_NUMBER_CONST * seed) + ((i4 * number) * i4 * i4 * i4 * i4 * i4 * i4 * i4 * RANDOM_NUMBER_CONST * seed) + (number * number * number * number * number * number * number * number * RANDOM_NUMBER_CONST * seed)) / (123135431L + (i1 + i2 + i3 + i4 + number));
		return toReturn;
	}
}

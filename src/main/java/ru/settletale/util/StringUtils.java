package ru.settletale.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import ru.settletale.memory.MemoryBlock;

public class StringUtils {
	public static final byte RIGHT = 0;
	public static final byte LEFT = 1;
	public static final byte E = 2;

	public static void main(String[] args) {
		//String str = "0.456 5.456 8646.87987 654.56465 6546.4444";//"1/2 3/4 5/3";

		//System.out.println(Float.toHexString(f));
		/*int[][] arr = new int[3][6];

		readInts(str, arr, ' ', '/', -1);

		for (int i = 0; i < 3; i++) {
			for (int z = 0; z < 2; z++) {
				System.out.println(arr[i][z]);
			}
		}*/
		
		/*float[] arr = new float[5];
		readFloats(str, arr);
		
		for (int i = 0; i < 5; i++) {
			System.out.println(arr[i]);
		}*/
		
		forEachFloatValue("-4.37114e-8", (int index, float val) -> {
			System.out.println(val + " ");
		});
	}

	public static boolean isSharpCommentLine(String str) {
		return str.startsWith("#");
	}

	public static boolean isEmptyOrSpacedLine(String str) {
		if (str.isEmpty()) {
			return true;
		}

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != ' ') {
				return false;
			}
		}

		return false;
	}
	
	@FunctionalInterface
	public static interface IFloatIterFunc {
		public void iter(int index, float val);
	}
	
	@FunctionalInterface
	public static interface IIntIterFunc {
		public void iter(int index, int val);
	}
	
	public static int forEachFloatValue(String str, IFloatIterFunc func) {
		byte part = LEFT;

		float val = 0;

		float sign = 1F;
		boolean signE = false;
		int numPow = 1;
		float num = 0;
		int count = 0;
		boolean prevWasNum = false;

		int len = str.length();

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			if (ch >= '0' && ch <= '9') {
				num *= 10;
				num += ch - '0';
				numPow *= 10;
				prevWasNum = true;
			}
			else if (ch == '-') {
				if (!prevWasNum) {
					sign = -1;
				}
				else {
					signE = true;
				}
			}
			else if (ch == '.') {
				if (part == LEFT) {
					part = RIGHT;
					numPow = 1;
					val = num;
					num = 0;
				}
				else {
					throw new Error("Two dots in one number?!");
				}
			}
			else if (ch == 'e') {
				if (part == RIGHT) {
					part = E;
					val += num / (float) numPow;
					num = 0;
				}
			}
			else {
				if (prevWasNum) {
					if (part == LEFT) {
						val = num;
					}
					else if (part == RIGHT) {
						val += num / (float) numPow;
					}
					else if (part == E) {
						val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
					}

					func.iter(count++, val * sign);
					
					sign = 1F;
					signE = false;
					num = 0;
					numPow = 1;
					val = 0;
					part = LEFT;
					prevWasNum = false;
				}
			}

		}

		if (prevWasNum) {
			if (part == RIGHT) {
				val += num / (float) numPow;
			}
			else if (part == E) {
				val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
			}

			func.iter(count++, val * sign);
		}

		return count;
	}

	/** Splitter is ' ' **/
	public static int readFloats(String str, float[] arr) {
		return forEachFloatValue(str, (index, val) -> arr[index++] = val);
	}
	
	public static int readFloats(String str, FloatBuffer fb) {
		return forEachFloatValue(str, (index, val) -> fb.put(index, val));
	}
	
	public static int readFloats(String str, ByteBuffer fb) {
		return forEachFloatValue(str, (index, val) -> fb.putFloat(index * Float.BYTES, val));
	}
	
	public static int readFloats(String str, MemoryBlock mb) {
		return forEachFloatValue(str, (index, val) -> mb.putFloatF(index, val));
	}

	public static int forEachIntValue(String str, IIntIterFunc func) {
		int sign = 1;
		int num = 0;
		int count = 0;
		boolean prevWasNum = false;

		int len = str.length();

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			if (ch >= '0' && ch <= '9') {
				num *= 10;
				num += ch - '0';
				prevWasNum = true;
			}
			else if (ch == '-' && !prevWasNum) {
				sign = -1;
			}
			else {
				if (prevWasNum) {
					func.iter(count++, num * sign);
					sign = 1;
					num = 0;
				}

				prevWasNum = false;
			}
		}
		
		if (prevWasNum) {
			func.iter(count++, num * sign);
		}

		return count;
	}
	
	public static int readInts(String str, IntBuffer ib) {
		return forEachIntValue(str, (index, val) -> ib.put(index, val));
	}
	
	/** Example: 1/2*3/4*5/3. Here, s1 - '*', s2 - '/'. Def uses if number between splits is undefinded. **/
	public static int readInts(String str, int[][] arr, char externalSplitter, char interiorSplitter, int def) {
		boolean sign = false;
		int num = 0;
		int count = 0;
		int interiorsCount = 0;
		int externalsCount = 0;
		boolean prevWasNum = false;

		int len = str.length();

		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);

			if (ch >= '0' && ch <= '9') {
				num *= 10;
				num += ch - '0';
				prevWasNum = true;
			}
			else if (ch == '-' && !prevWasNum) {
				sign = true;
			}
			else if (ch == externalSplitter || ch == interiorSplitter) {
				if (prevWasNum) {
					arr[externalsCount][interiorsCount++] = num * (sign ? -1 : 1);
					count++;
					sign = false;
					num = 0;
				}
				else {
					arr[externalsCount][interiorsCount++] = def;
				}

				if (ch == externalSplitter) {
					externalsCount++;
					interiorsCount = 0;
				}

				prevWasNum = false;
			}
		}
		
		if (prevWasNum) {
			arr[externalsCount][interiorsCount++] = num * (sign ? -1 : 1);
			count++;
		}
		else {
			arr[externalsCount][interiorsCount++] = def;
		}

		return count;
	}
}

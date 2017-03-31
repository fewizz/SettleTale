package ru.settletale.util;

public class StringUtils {
	public static final byte RIGHT = 0;
	public static final byte LEFT = 1;
	public static final byte E = 2;
	
	public static final float f = 0.546F;

	public static void main(String[] args) {
		String str = "0.456 5.456 8646.87987 654.56465 6546.4444";//"1/2 3/4 5/3";

		System.out.println(Float.toHexString(f));
		/*int[][] arr = new int[3][6];

		readInts(str, arr, ' ', '/', -1);

		for (int i = 0; i < 3; i++) {
			for (int z = 0; z < 2; z++) {
				System.out.println(arr[i][z]);
			}
		}*/
		
		float[] arr = new float[5];
		readFloats(str, arr);
		
		for (int i = 0; i < 5; i++) {
			System.out.println(arr[i]);
		}
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

	public static int readFloats(String str, float[] arr) {
		byte part = LEFT;

		float val = 0;

		boolean sign = false;
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
					sign = true;
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
					if (part == RIGHT) {
						val += num / (float) numPow;
					}
					else if (part == E) {
						val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
					}

					arr[count++] = val * (sign ? -1 : 1);
					sign = false;
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

			arr[count++] = val * (sign ? -1 : 1);
		}

		return count;
	}

	/** Example: 1/2*3/4*5/3. Here, s1 - '*', s2 - '/'. Def uses if number between splits is undefinded. **/
	public static int readInts(String str, int[][] arr, char split1, char split2, int def) {
		boolean sign = false;
		int num = 0;
		int count = 0;
		int index = 0;
		int mainIndex = 0;
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
			else if (ch == split1 || ch == split2) {
				if (prevWasNum) {
					arr[mainIndex][index++] = num * (sign ? -1 : 1);
					count++;
					sign = false;
					num = 0;
				}
				else {
					arr[mainIndex][index++] = def;
				}

				if (ch == split1) {
					mainIndex++;
					index = 0;
				}

				prevWasNum = false;
			}
		}
		
		if (prevWasNum) {
			arr[mainIndex][index++] = num * (sign ? -1 : 1);
			count++;
		}
		else {
			arr[mainIndex][index++] = def;
		}

		return count;
	}
}

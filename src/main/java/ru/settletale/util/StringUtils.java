package ru.settletale.util;

import java.util.Arrays;

public class StringUtils {
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
		PrimitivePart part = PrimitivePart.LEFT;

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
				if (part == PrimitivePart.LEFT) {
					part = PrimitivePart.RIGHT;
					numPow = 1;
					val = num;
					num = 0;
				}
			}
			else if (ch == 'e') {
				if (part == PrimitivePart.RIGHT) {
					part = PrimitivePart.E;
					val += num / (float) numPow;
					num = 0;
				}
			}
			else {
				if (prevWasNum) {
					if (part == PrimitivePart.RIGHT) {
						val += num / (float) numPow;
					}
					else if (part == PrimitivePart.E) {
						val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
					}

					arr[count++] = val * (sign ? -1 : 1);
					sign = false;
					signE = false;
					num = 0;
					numPow = 1;
					val = 0;
					part = PrimitivePart.LEFT;
					prevWasNum = false;
				}
			}

		}

		if (prevWasNum) {
			if (part == PrimitivePart.RIGHT) {
				val += num / (float) numPow;
			}
			else if (part == PrimitivePart.E) {
				val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
			}

			arr[count++] = val * (sign ? -1 : 1);
		}

		return count;
	}

	public static int readFloats(String str, float[][] arr, char split1, char split2) {
		PrimitivePart part = PrimitivePart.LEFT;

		float val = 0;

		boolean sign = false;
		boolean signE = false;
		int numPow = 1;
		float num = 0;
		int count = 0;
		int mainIndex = 0;
		int index = 0;
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
				if (part == PrimitivePart.LEFT) {
					part = PrimitivePart.RIGHT;
					numPow = 1;
					val = num;
					num = 0;
				}
			}
			else if (ch == 'e') {
				if (part == PrimitivePart.RIGHT) {
					part = PrimitivePart.E;
					val += num / (float) numPow;
					num = 0;
				}
			}
			else if (ch == split1) {
				mainIndex++;
				index = 0;
			}
			else if (ch == split2) {
				if (part == PrimitivePart.RIGHT) {
					val += num / (float) numPow;
				}
				else if (part == PrimitivePart.E) {
					val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
				}

				arr[mainIndex][index++] = val * (sign ? -1 : 1);
				count++;
				sign = false;
				signE = false;
				num = 0;
				numPow = 1;
				val = 0;
				part = PrimitivePart.LEFT;

				prevWasNum = false;
			}

		}

		if (prevWasNum) {
			if (part == PrimitivePart.RIGHT) {
				val += num / (float) numPow;
			}
			else if (part == PrimitivePart.E) {
				val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
			}

			arr[mainIndex][index++] = val * (sign ? -1 : 1);
			count++;
		}

		return count;
	}

	public static int readInts(String str, int[][] arr, char split1, char split2, int def) {
		PrimitivePart part = PrimitivePart.LEFT;
		for(int i = 0; i < arr.length; i++) {
			Arrays.fill(arr[i], -1);
		}

		int val = 0;

		boolean sign = false;
		boolean signE = false;
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
			else if (ch == '-') {
				if (!prevWasNum) {
					sign = true;
				}
				else {
					signE = true;
				}
			}
			else if (ch == 'e') {
				if (part == PrimitivePart.LEFT) {
					part = PrimitivePart.E;
					val = num;
					num = 0;
				}
			}
			else if (ch == split2 || ch == split1) {
				if (prevWasNum) {
					if (part == PrimitivePart.LEFT) {
						val = num;
					}
					else if (part == PrimitivePart.E) {
						val = (int) (val * Math.pow(10, num * (signE ? -1 : 1)));
					}

					arr[mainIndex][index++] = val * (sign ? -1 : 1);
					count++;
					sign = false;
					signE = false;
					num = 0;
					val = 0;
					part = PrimitivePart.LEFT;
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
			if (part == PrimitivePart.LEFT) {
				val = num;
			}
			else if (part == PrimitivePart.E) {
				val = (int) (val * Math.pow(10, num * (signE ? -1 : 1)));
			}

			arr[mainIndex][index++] = val * (sign ? -1 : 1);
			count++;
		}
		else {
			arr[mainIndex][index++] = def;
		}

		return count;
	}

	enum PrimitivePart {
		LEFT,
		RIGHT,
		E
	}
}

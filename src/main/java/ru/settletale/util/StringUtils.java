package ru.settletale.util;

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
		PrimitivePart part = PrimitivePart.Left;

		float val = 0;

		boolean sign = false;
		boolean signE = false;
		int numPow = 1;
		float num = 0;
		int lastIndex = 0;
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
				if (part == PrimitivePart.Left) {
					part = PrimitivePart.Right;
					numPow = 1;
					val = num;
					num = 0;
				}
			}
			else if (ch == 'e') {
				if (part == PrimitivePart.Right) {
					part = PrimitivePart.E;
					val += num / (float) numPow;
					num = 0;
				}
			}
			else if (ch >= '0' && ch <= '9') {
				num *= 10;
				num += ch - '0';
				numPow *= 10;
				prevWasNum = true;
			}
			else {
				if (prevWasNum) {
					if (part == PrimitivePart.Right) {
						val += num / (float) numPow;
					}
					else if (part == PrimitivePart.E) {
						val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
					}

					arr[lastIndex++] = val * (sign ? -1 : 1);
					sign = false;
					signE = false;
					num = 0;
					numPow = 1;
					val = 0;
					part = PrimitivePart.Left;
				}

				prevWasNum = false;
			}

		}

		if (prevWasNum) {
			if (part == PrimitivePart.Right) {
				val += num / (float) numPow;
			}
			else if (part == PrimitivePart.E) {
				val = val * (float) Math.pow(10, num * (signE ? -1 : 1));
			}

			arr[lastIndex++] = val * (sign ? -1 : 1);
		}

		return lastIndex;
	}
	
	public static int readInts(String str, int[] arr) {
		PrimitivePart part = PrimitivePart.Left;

		int val = 0;

		boolean sign = false;
		boolean signE = false;
		int num = 0;
		int lastIndex = 0;
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
				if (part == PrimitivePart.Left) {
					part = PrimitivePart.E;
					val = num;
					num = 0;
				}
			}
			else if (ch >= '0' && ch <= '9') {
				num *= 10;
				num += ch - '0';
				prevWasNum = true;
			}
			else {
				if (prevWasNum) {
					if (part == PrimitivePart.Left) {
						val = num;
					}
					else if (part == PrimitivePart.E) {
						val = (int) (val * Math.pow(10, num * (signE ? -1 : 1)));
					}

					arr[lastIndex++] = val * (sign ? -1 : 1);
					sign = false;
					signE = false;
					num = 0;
					val = 0;
					part = PrimitivePart.Left;
				}

				prevWasNum = false;
			}

		}

		if (prevWasNum) {
			if (part == PrimitivePart.Left) {
				val = num;
			}
			else if (part == PrimitivePart.E) {
				val = (int) (val * Math.pow(10, num * (signE ? -1 : 1)));
			}

			arr[lastIndex++] = val * (sign ? -1 : 1);
		}

		return lastIndex;
	}

	enum PrimitivePart {
		Left,
		Right,
		E
	}
}

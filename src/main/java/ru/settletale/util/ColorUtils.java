package ru.settletale.util;

import java.awt.Color;

public class ColorUtils {
	public static int getARGB(Color color) {
		return color.getRGB() | (color.getAlpha() << 24);
	}
}

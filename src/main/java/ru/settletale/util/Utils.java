package ru.settletale.util;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class Utils {
	/** Unsafe stuff **/
	public static Unsafe u;

	static {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");

			f.setAccessible(true);

			u = (Unsafe) f.get(null);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} 

	}
}

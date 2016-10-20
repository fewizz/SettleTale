package ru.settletale.util;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import sun.misc.Cleaner;

public class ByteBufferUtils {
	public static Class<?> bitsClass;
	public static Class<?> deallClass;
	public static Field addressField;
	public static Field dAddr;
	public static Field dSize;
	public static Field dCap;
	
	static {
		try {
			bitsClass = Class.forName("java.nio.Bits");
			addressField = Buffer.class.getDeclaredField("address");
			addressField.setAccessible(true);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateDealloc(ByteBuffer buff, long add, int cap) {
		try {
			Field d = buff.getClass().getDeclaredField("cleaner");
			d.setAccessible(true);
			Cleaner c = (Cleaner) d.get(buff);
			
			Field t = Cleaner.class.getDeclaredField("thunk");
			t.setAccessible(true);
			
			if(deallClass == null) {
				deallClass = t.get(c).getClass();
				
				dAddr = deallClass.getDeclaredField("address");
				dAddr.setAccessible(true);
				
				dSize = deallClass.getDeclaredField("size");
				dSize.setAccessible(true);
				
				dCap = deallClass.getDeclaredField("capacity");
				dCap.setAccessible(true);
			}
			
			dAddr.setLong(t.get(c), add);
			dSize.setLong(t.get(c), cap);
			dCap.setInt(t.get(c), cap);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static Field getAddressField() {
		return addressField;
	}
}

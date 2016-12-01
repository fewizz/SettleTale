package ru.settletale.util;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

public class DirectByteBufferUtils {
	public static Class<?> bitsClass;
	public static Class<?> deallClass;
	public static Field addressField;
	public static Field dAddr;
	public static Field dSize;
	public static Field dCap;
	private static long bufferAddressFieldOffset;
	static Unsafe u;
	
	static {
		u = Utils.u;
		
		try {
			bitsClass = Class.forName("java.nio.Bits");
			addressField = Buffer.class.getDeclaredField("address");
			addressField.setAccessible(true);
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		
		bufferAddressFieldOffset = u.objectFieldOffset(addressField);
	}
	
	public static long getBufferAddress(ByteBuffer buff) {
		return u.getLong(buff, bufferAddressFieldOffset);
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
	
	public static long updateBufferSize(ByteBuffer buff, int size, long addr) {
		try {
			Field cap;
			cap = Buffer.class.getDeclaredField("capacity");
			cap.setAccessible(true);
			cap.setInt(buff, size);
			u.freeMemory(addr);
			long naddr = u.allocateMemory(size);
			u.putLong(buff, bufferAddressFieldOffset, naddr);
			buff.limit(size);
			buff.position(0);
			updateDealloc(buff, naddr, size);
			return naddr;
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static ByteBuffer grow(ByteBuffer buff, float factor) {
		ByteBuffer nbb = BufferUtils.createByteBuffer((int) (buff.capacity() * factor));
		nbb.put(buff);
		nbb.position(0);
		nbb.limit(buff.limit());
		
		System.gc();
		
		return nbb;
	}
	
	public static Field getAddressField() {
		return addressField;
	}
}

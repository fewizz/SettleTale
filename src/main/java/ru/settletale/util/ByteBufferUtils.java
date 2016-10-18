package ru.settletale.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

public class ByteBufferUtils {
	public static Class<?> bitsClass;
	
	static {
		try {
			bitsClass = Class.forName("java.nio.Bits");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void setDealloc(ByteBuffer buff, long add, int cap) {
		try {
			Field d = buff.getClass().getDeclaredField("cleaner");
			d.setAccessible(true);
			Cleaner c = (Cleaner) d.get(buff);
			
			Field t = Cleaner.class.getDeclaredField("thunk");
			t.setAccessible(true);
			t.set(c, new Deallocator(add, cap, cap));

		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	static class Deallocator implements Runnable {
		private static Unsafe unsafe;
		private long address;
		private long size;
		private int capacity;

		private Deallocator(final long address, final long size, final int capacity) {
			assert address != 0L;
			this.address = address;
			this.size = size;
			this.capacity = capacity;
		}

		@Override
		public void run() {
			if (this.address == 0L) {
				return;
			}
			Deallocator.unsafe.freeMemory(this.address);
			this.address = 0L;
			try {
				Method m = bitsClass.getDeclaredMethod("unreserveMemory", long.class, int.class);
				m.setAccessible(true);
				m.invoke(null, this.size, this.capacity);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		static {
			Deallocator.unsafe = FloatPrimitiveList.u;
		}
	}
}

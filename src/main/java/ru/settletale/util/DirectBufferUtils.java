package ru.settletale.util;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public class DirectBufferUtils {
	static Field fieldAddress;
	static Field fieldCapacity;
	
	static {
		try {
			fieldAddress = Buffer.class.getDeclaredField("address");
			fieldCapacity = Buffer.class.getDeclaredField("capacity");
			
			fieldAddress.setAccessible(true);
			fieldCapacity.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static ByteBuffer growBuffer(ByteBuffer buff, float scale) {
		return MemoryUtil.memRealloc(buff, (int) (buff.capacity() * scale));
	}
	
	public static ByteBuffer getByteBufferView(FloatBuffer buffer) {
		return MemoryUtil.memByteBuffer(MemoryUtil.memAddress(buffer), buffer.capacity() * Float.BYTES);
	}
	
	public static void copyInfo(Buffer src, Buffer dst) {
		try {
			fieldCapacity.setInt(dst, src.capacity());
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		dst.limit(src.limit());
		dst.position(src.position());
		setAddress(src, dst);
	}
	
	public static void setAddress(Buffer src, Buffer dst) {
		setAddress(dst, MemoryUtil.memAddress0(src));
	}
	
	public static void setAddress(Buffer buff, long address) {
		try {
			DirectBufferUtils.fieldAddress.setLong(buff, address);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}

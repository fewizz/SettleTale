package ru.settletale.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public class DirectBufferUtils {
	
	public static ByteBuffer growBuffer(ByteBuffer buff, float scale) {
		return MemoryUtil.memRealloc(buff, (int) (buff.capacity() * scale));
	}
	
	public static ByteBuffer fromFloatToByteBufferView(FloatBuffer buffer) {
		return MemoryUtil.memByteBuffer(MemoryUtil.memAddress(buffer), buffer.capacity() * Float.BYTES);
	}
}

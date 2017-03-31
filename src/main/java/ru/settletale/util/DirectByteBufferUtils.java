package ru.settletale.util;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

public class DirectByteBufferUtils {
	
	public static ByteBuffer growBuffer(ByteBuffer buff, float scale) {
		return MemoryUtil.memRealloc(buff, (int) (buff.capacity() * scale));
	}
}

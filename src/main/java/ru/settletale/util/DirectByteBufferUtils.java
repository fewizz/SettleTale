package ru.settletale.util;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

public class DirectByteBufferUtils {
	
	public static void growBuffer(ByteBuffer buff, float scale) {
		MemoryUtil.memRealloc(buff, (int) (buff.capacity() * scale));
	}
}

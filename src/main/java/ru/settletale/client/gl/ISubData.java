package ru.settletale.client.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public interface ISubData {
	public void subData(ByteBuffer buffer);
	
	default public void subData(FloatBuffer buffer) {
		subData(MemoryUtil.memByteBuffer(MemoryUtil.memAddress(buffer), buffer.capacity() * Float.BYTES));
	}
}

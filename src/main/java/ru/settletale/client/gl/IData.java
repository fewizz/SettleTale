package ru.settletale.client.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public interface IData {
	public void data(ByteBuffer buffer);

	default public void data(FloatBuffer buffer) {
		data(MemoryUtil.memByteBuffer(MemoryUtil.memAddress(buffer), buffer.capacity() * Float.BYTES));
	}
}

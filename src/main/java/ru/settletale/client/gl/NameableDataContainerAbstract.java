package ru.settletale.client.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public abstract class NameableDataContainerAbstract<T> extends NameableAbstract<T> {
	public abstract void loadData(ByteBuffer buffer);
	public abstract void loadSubData(ByteBuffer buffer);
	
	public void loadData(FloatBuffer buffer) {
		loadData(MemoryUtil.memByteBuffer(MemoryUtil.memAddress(buffer), buffer.capacity() * 4));
	}
	
	public void loadSubData(FloatBuffer buffer) {
		loadSubData(MemoryUtil.memByteBuffer(MemoryUtil.memAddress(buffer), buffer.capacity() * 4));
	}
}

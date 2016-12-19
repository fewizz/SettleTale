package ru.settletale.client.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public abstract class NameableDataContainerAdapter<T> extends NameableAdapter<T> implements IBufferContainer<T> {
	public ByteBuffer buffer;

	public T buffer(ByteBuffer buffer) {
		this.buffer = buffer;
		return getThis();
	}

	public T buffer(FloatBuffer buffer) {
		this.buffer = MemoryUtil.memByteBuffer(MemoryUtil.memAddress(buffer), buffer.capacity() * Float.BYTES);
		return getThis();
	}

	@Override
	public abstract T loadData();

	@Override
	public abstract T loadSubData();
}

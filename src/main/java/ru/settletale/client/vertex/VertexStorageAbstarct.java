package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public abstract class VertexStorageAbstarct implements IVertexStorage {
	final int size;
	ByteBuffer buff;
	
	public VertexStorageAbstarct(int size) {
		this.size = size;
		buff = BufferUtils.createByteBuffer(4096);
		buff.limit(0);
	}
	
	@Override
	public void data(byte b1, byte b2, byte b3, byte b4) {
		// Nope
	}
	
	@Override
	public void data(float f1, float f2, float f3, float f4) {
		// Nope
	}

	@Override
	public ByteBuffer getBuffer() {
		return buff;
	}

	@Override
	public void clear() {
		buff.clear();
	}

}

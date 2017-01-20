package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

public abstract class VertexStorageAbstarct {
	final int size;
	ByteBuffer buff;

	public VertexStorageAbstarct(int size) {
		this.size = size;
		buff = MemoryUtil.memAlloc(4096);
		buff.limit(0);
	}

	public void data(byte b1, byte b2, byte b3, byte b4) {
	}

	public void data(float f1, float f2, float f3, float f4) {
	}
	
	public abstract void dataEnd(int id);

	public ByteBuffer getBuffer() {
		return buff;
	}

	public void clear() {
		buff.clear();
	}

}

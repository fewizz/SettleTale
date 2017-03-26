package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.util.PrimitiveType;

public abstract class AttributeStorageAbstarct {
	final int count;
	final int growBytes;
	ByteBuffer buff;
	PrimitiveType primitiveType;

	public AttributeStorageAbstarct(int size, PrimitiveType primitiveType) {
		this.count = size;
		this.primitiveType = primitiveType;
		this.growBytes = size * primitiveType.getSizeInBytes();
		buff = MemoryUtil.memAlloc(4096);
		buff.limit(0);
	}

	public void data(byte b1, byte b2, byte b3, byte b4) {
	}
	
	public void data(short s1, short s2, short s3, short s4) {
	}

	public void data(float f1, float f2, float f3, float f4) {
	}
	
	public void data(int i1, int i2, int i3, int i4) {
	}
	
	public abstract void dataEnd(int id);

	public ByteBuffer getBuffer() {
		return buff;
	}

	public void clear() {
		buff.clear();
	}
	
	public void delete() {
		MemoryUtil.memFree(buff);
	}

}

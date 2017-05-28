package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.util.DirectBufferUtils;

public abstract class AttribArrayData {
	final VertexAttribType attribType;
	final int growBytes;
	ByteBuffer buff;
	final int vertexCount;
	final boolean isDynamic;

	public AttribArrayData(int vertexCount, boolean dynamic, VertexAttribType attribType) {
		this.vertexCount = vertexCount;
		this.attribType = attribType;
		this.growBytes = attribType.perVertexElementCount * attribType.clientDataType.getSizeInBytes();
		buff = MemoryUtil.memAlloc(vertexCount * growBytes);
		buff.limit(0);
		this.isDynamic = dynamic;
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
	
	protected void growIfNeed(int index) {
		if(index >= buff.capacity()) {
			if(!isDynamic) {
				throw new Error("Max index reached");
			}
			buff = DirectBufferUtils.growBuffer(buff, 1.5F);
		}
	}

}

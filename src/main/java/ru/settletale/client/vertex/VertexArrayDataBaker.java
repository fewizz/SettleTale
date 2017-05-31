package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class VertexArrayDataBaker {
	private final IntObjMap<AttribArrayData> attributes = HashIntObjMaps.newMutableMap(0);
	int lastVertexIndex = -1;
	int maxVertexCount;
	float growFactor = 1.5F;
	protected final boolean dynamic;
	
	public VertexArrayDataBaker(int expectedVertexCount, boolean dynamic, VertexAttribType... attribTypes) {
		this.dynamic = dynamic;
		this.maxVertexCount = expectedVertexCount;
		
		for(int attribIndex = 0; attribIndex < attribTypes.length; attribIndex++) {
			addStorage(attribTypes[attribIndex], attribIndex);
		}
	}

	public VertexArrayDataBaker addStorage(VertexAttribType at, int index) {
		attributes.put(index, at.getNewVertexAttribArrayBuffer(this));
		return this;
	}

	public void endVertex() {
		lastVertexIndex++;
		
		if(dynamic && lastVertexIndex >= maxVertexCount - 1) {
			if(!dynamic) {
				throw new IndexOutOfBoundsException();
			}
			attributes.forEach((int index, AttribArrayData data) -> data.grow(growFactor));
			maxVertexCount *= growFactor;
		}
		
		attributes.forEach((int index, AttribArrayData data) -> data.dataEnd());
	}

	public void clear() {
		attributes.forEach((int index, AttribArrayData data) -> data.clear());
		lastVertexIndex = -1;
	}

	public void delete() {
		clear();
		attributes.forEach((int index, AttribArrayData data) -> data.delete());
	}

	public int getMaxVertexCount() {
		return this.maxVertexCount;
	}
	
	public int getUsedVertexCount() {
		return this.lastVertexIndex + 1;
	}

	public void putFloat(int index, float f1, float f2, float f3, float f4) {
		attributes.get(index).data(f1, f2, f3, f4);
	}

	public void putFloat(int index, float f1) {
		this.putFloat(index, f1, 0F, 0F, 0F);
	}

	public void putFloat(int index, float f1, float f2, float f3) {
		this.putFloat(index, f1, f2, f3, 0F);
	}

	public void putFloat(int index, float f1, float f2) {
		this.putFloat(index, f1, f2, 0F, 0F);
	}

	public void putInt(int index, int i1, int i2, int i3, int i4) {
		attributes.get(index).data(i1, i2, i3, i4);
	}

	public void putInt(int index, int i1) {
		this.putInt(index, i1, 0, 0, 0);
	}

	public void putShort(int index, short s1) {
		putShort(index, s1, (short) 0, (short) 0, (short) 0);
	}

	public void putShort(int index, short s1, short s2, short s3, short s4) {
		attributes.get(index).data(s1, s2, s3, s4);
	}

	public void putByte(int index, byte b1, byte b2, byte b3, byte b4) {
		attributes.get(index).data(b1, b2, b3, b4);
	}

	public void putByte(int index, byte b1) {
		this.putByte(index, b1, (byte) 0, (byte) 0, (byte) 0);
	}
	
	public VertexAttribType getAttribType(int index) {
		return attributes.get(index).attribType;
	}

	public int getAttributeCount() {
		return attributes.size();
	}
	
	public ByteBuffer getBuffer(int attributeLocation) {
		return attributes.get(attributeLocation).getBuffer();
	}
}
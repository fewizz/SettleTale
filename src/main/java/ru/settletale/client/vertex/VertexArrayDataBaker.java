package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.joml.Vector4f;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class VertexArrayDataBaker {
	private final IntObjMap<AttribArrayData> attributes = HashIntObjMaps.newMutableMap(0);
	int lastVertexIndex = -1;
	int maxVertexCount;
	float growFactor = 1.5F;
	protected final boolean dynamic;
	
	public VertexArrayDataBaker(int expectedVertexCount, boolean dynamic) {
		this.dynamic = dynamic;
		this.maxVertexCount = expectedVertexCount;
	}
	
	public VertexArrayDataBaker(int expectedVertexCount, boolean dynamic, VertexAttribType... attribTypes) {
		this(expectedVertexCount, dynamic);
		
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

	public void putFloats(int index, Vector4f v) {
		putFloats(index, v.x, v.y, v.z, v.w);
	}
	
	public void putFloats(int index, float[] fArray, int elementCount) {
		switch (elementCount) {
			case 1:
				putFloats(index, fArray[0]);
				return;
			case 2:
				putFloats(index, fArray[0], fArray[1]);
				return;
			case 3:
				putFloats(index, fArray[0], fArray[1], fArray[2]);
				return;
			case 4:
				putFloats(index, fArray[0], fArray[1], fArray[2], fArray[3]);
				return;
			default:
				break;
		}
	}
	
	public void putFloats(int index, float f1, float f2, float f3, float f4) {
		attributes.get(index).data(f1, f2, f3, f4);
	}

	public void putFloats(int index, float f1) {
		this.putFloats(index, f1, 0F, 0F, 0F);
	}

	public void putFloats(int index, float f1, float f2, float f3) {
		this.putFloats(index, f1, f2, f3, 0F);
	}

	public void putFloats(int index, float f1, float f2) {
		this.putFloats(index, f1, f2, 0F, 0F);
	}

	public void putInts(int index, int i1, int i2, int i3, int i4) {
		attributes.get(index).data(i1, i2, i3, i4);
	}

	public void putInts(int index, int i1) {
		this.putInts(index, i1, 0, 0, 0);
	}

	public void putShorts(int index, short s1) {
		putShorts(index, s1, (short) 0, (short) 0, (short) 0);
	}

	public void putShorts(int index, short s1, short s2, short s3, short s4) {
		attributes.get(index).data(s1, s2, s3, s4);
	}

	public void putBytes(int index, byte b1, byte b2, byte b3, byte b4) {
		attributes.get(index).data(b1, b2, b3, b4);
	}

	public void putBytes(int index, byte b1) {
		this.putBytes(index, b1, (byte) 0, (byte) 0, (byte) 0);
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
	
	public void forEachAttribDataArray(IAttrIterFunc func) {
		attributes.forEach((int index, AttribArrayData data) -> {
			func.iter(index, data);
		});
	}
	
	@FunctionalInterface
	public static interface IAttrIterFunc {
		public void iter(int index, AttribArrayData data);
	}
}
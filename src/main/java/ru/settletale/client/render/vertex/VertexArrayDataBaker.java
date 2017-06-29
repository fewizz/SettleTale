package ru.settletale.client.render.vertex;

import java.nio.ByteBuffer;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.function.IntObjConsumer;

import ru.settletale.memory.MemoryBlock;

public class VertexArrayDataBaker {
	private final IntObjMap<AttribArrayData> attributes = HashIntObjMaps.newMutableMap(2);
	int vertexCount = 0;
	int maxVertexCount;
	float growFactor = 1.5F;
	protected final boolean dynamic;

	public VertexArrayDataBaker(int expectedVertexCount, boolean dynamic) {
		this.dynamic = dynamic;
		this.maxVertexCount = expectedVertexCount;
	}

	public VertexArrayDataBaker(int expectedVertexCount, boolean dynamic, VertexAttribType... attribTypes) {
		this(expectedVertexCount, dynamic);

		for (int attribIndex = 0; attribIndex < attribTypes.length; attribIndex++) {
			addAttrib(attribTypes[attribIndex], attribIndex);
		}
	}

	public VertexArrayDataBaker addAttrib(VertexAttribType at, int index) {
		attributes.put(index, new AttribArrayData(at, maxVertexCount));
		return this;
	}

	public void endVertex() {
		checkAndGrowIfNeed();

		attributes.forEach((int index, AttribArrayData data) -> data.endAttrib());
		vertexCount++;
	}

	private void checkAndGrowIfNeed() {
		if (vertexCount + (dynamic ? 1 : 0) >= maxVertexCount) {
			if (!dynamic) {
				throw new IndexOutOfBoundsException();
			}
			maxVertexCount *= growFactor;
			attributes.forEach((int ind, AttribArrayData data) -> data.growIfNeed(maxVertexCount));
		}
	}

	public void clear() {
		attributes.forEach((int index, AttribArrayData data) -> data.reset());
		reset();
	}
	
	public void reset() {
		vertexCount = 0;
		attributes.forEach((int index, AttribArrayData data) -> data.reset());
	}

	public void delete() {
		clear();
		attributes.forEach((int index, AttribArrayData data) -> data.delete());
	}

	public int getMaxVertexCount() {
		return this.maxVertexCount;
	}

	public int getUsedVertexCount() {
		return this.vertexCount;
	}

	public void putFloats(int index, Vector4f v) {
		putFloats(index, v.x, v.y, v.z, v.w);
	}

	public void putFloats(int index, Vector3f v) {
		putFloats(index, v.x, v.y, v.z, 1F);
	}

	public void putFloats(int index, Vector2f v) {
		putFloats(index, v.x, v.y, 0F, 1F);
	}

	public void putFloats(int index, float f1, float f2, float f3, float f4) {
		MemoryBlock mb = getAttribArrayData(index).getCurrentAttribMemoryBlock();
		mb.putFloatF(0, f1);
		mb.putFloatF(1, f2);
		mb.putFloatF(2, f3);
		mb.putFloatF(3, 14);
	}

	public void putFloat(int index, float f1) {
		this.putFloats(index, f1, 0F, 0F, 1F);
	}

	public void putFloats(int index, float f1, float f2, float f3) {
		putFloats(index, f1, f2, f3, 1F);
	}

	public void putFloats(int index, float f1, float f2) {
		this.putFloats(index, f1, f2, 0F, 1F);
	}

	public void putInts(int index, int i1, int i2, int i3, int i4) {
		getAttribArrayData(index).data(i1, i2, i3, i4);
	}

	public void putInt(int index, int i1) {
		this.putInts(index, i1, 0, 0, 0);
	}

	public void putShorts(int index, short s1) {
		putShorts(index, s1, (short) 0, (short) 0, (short) 0);
	}

	public void putShorts(int index, short s1, short s2, short s3, short s4) {
		getAttribArrayData(index).data(s1, s2, s3, s4);
	}

	public void putBytes(int index, byte b1, byte b2, byte b3, byte b4) {
		MemoryBlock mb = getAttribArrayData(index).getCurrentAttribMemoryBlock();
		mb.putByte(0, b1);
		mb.putByte(1, b2);
		mb.putByte(2, b3);
		mb.putByte(3, b4);
	}

	public void putByte(int index, byte b1) {
		this.putBytes(index, b1, (byte) 0, (byte) 0, (byte) 0);
	}

	public VertexAttribType getAttribType(int index) {
		return getAttribArrayData(index).attribType;
	}

	public int getAttributeCount() {
		return attributes.size();
	}

	public ByteBuffer getBuffer(int attributeLocation) {
		return getAttribArrayData(attributeLocation).getBuffer();
	}
	
	public AttribArrayData getAttribArrayData(int attributeLocation) {
		return attributes.get(attributeLocation);
	}

	public void forEachAttribDataArray(IntObjConsumer<AttribArrayData> func) {
		attributes.forEach(func);
	}
}
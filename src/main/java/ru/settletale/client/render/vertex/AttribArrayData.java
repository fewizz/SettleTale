package ru.settletale.client.render.vertex;

import java.nio.ByteBuffer;

import ru.settletale.memory.MemoryBlock;

public class AttribArrayData {
	final VertexAttribType attribType;
	final int growBytes;
	final MemoryBlock mb;
	final MemoryBlock mbAttribDataContainer;

	public AttribArrayData(VertexAttribType attribType, int vertexCount) {
		this.attribType = attribType;
		this.growBytes = attribType.componentCount * attribType.clientDataType.getSizeInBytes();
		mb = new MemoryBlock().allocate(vertexCount * growBytes);
		mbAttribDataContainer = new MemoryBlock().allocate(4 * Long.BYTES);
		init();
	}

	private void init() {
		mbAttribDataContainer.set(0);
		mb.set(0);
	}

	public void clear() {
		init();
	}

	public void delete() {
		mb.free();
		mbAttribDataContainer.free();
	}

	public void data(byte b1, byte b2, byte b3, byte b4) {
		if (attribType.clientDataType.getSizeInBytes() != Byte.BYTES) {
			throw new UnsupportedOperationException();
		}

		switch (attribType.componentCount) {
		case 4:
			mbAttribDataContainer.putByte(3, b4);
		case 3:
			mbAttribDataContainer.putByte(2, b3);
		case 2:
			mbAttribDataContainer.putByte(1, b2);
		case 1:
			mbAttribDataContainer.putByte(0, b1);

		default:
			break;
		}
	}

	public void data(short s1, short s2, short s3, short s4) {

	}

	public void data(float f1, float f2, float f3, float f4) {
		if (attribType.clientDataType.getSizeInBytes() != Float.BYTES) {
			throw new UnsupportedOperationException();
		}

		mbAttribDataContainer.putFloatF(3, f4);
		mbAttribDataContainer.putFloatF(2, f3);
		mbAttribDataContainer.putFloatF(1, f2);
		mbAttribDataContainer.putFloatF(0, f1);
	}

	public void data(int i1, int i2, int i3, int i4) {
		if (attribType.clientDataType.getSizeInBytes() != Integer.BYTES) {
			throw new UnsupportedOperationException();
		}

		mbAttribDataContainer.putIntI(3, i4);
		mbAttribDataContainer.putIntI(2, i3);
		mbAttribDataContainer.putIntI(1, i2);
		mbAttribDataContainer.putIntI(0, i1);
	}

	public void endVertex(int vertexIndex) {
		mbAttribDataContainer.copy(mb, 0, vertexIndex * growBytes, growBytes);
	}

	public ByteBuffer getBuffer(int vertexCount) {
		return mb.getAsByteBuffer(vertexCount * growBytes);
	}
	
	public MemoryBlock getAttribDataContainer() {
		return this.mbAttribDataContainer;
	}

	public void growIfNeed(int vertexCount) {
		if (mb.bytes() < vertexCount * growBytes) {
			mb.reallocate(vertexCount * growBytes);
		}
	}

}

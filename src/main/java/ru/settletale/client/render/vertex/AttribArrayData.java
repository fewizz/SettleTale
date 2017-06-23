package ru.settletale.client.render.vertex;

import java.nio.ByteBuffer;

import ru.settletale.memory.MemoryBlock;

public class AttribArrayData {
	final VertexAttribType attribType;
	final int growBytes;
	final MemoryBlock mb;
	final MemoryBlock mbCurrentAttrib;

	public AttribArrayData(VertexAttribType attribType, int vertexCount) {
		this.attribType = attribType;
		this.growBytes = attribType.componentCount * attribType.clientDataType.bytes;
		mb = new MemoryBlock().allocate(vertexCount * growBytes);
		mbCurrentAttrib = new MemoryBlock().allocate(4 * Long.BYTES);
		init();
	}

	private void init() {
		mbCurrentAttrib.set(0);
		mb.set(0);
	}

	public void clear() {
		init();
	}

	public void delete() {
		mb.free();
		mbCurrentAttrib.free();
	}

	public void data(byte b1, byte b2, byte b3, byte b4) {
		if (attribType.clientDataType.bytes != Byte.BYTES) {
			throw new UnsupportedOperationException();
		}

		switch (attribType.componentCount) {
		case 4:
			mbCurrentAttrib.putByte(3, b4);
		case 3:
			mbCurrentAttrib.putByte(2, b3);
		case 2:
			mbCurrentAttrib.putByte(1, b2);
		case 1:
			mbCurrentAttrib.putByte(0, b1);

		default:
			break;
		}
	}

	public void data(short s1, short s2, short s3, short s4) {

	}

	public void data(float f1, float f2, float f3, float f4) {
		if (attribType.clientDataType.bytes != Float.BYTES) {
			throw new UnsupportedOperationException();
		}

		mbCurrentAttrib.putFloatF(3, f4);
		mbCurrentAttrib.putFloatF(2, f3);
		mbCurrentAttrib.putFloatF(1, f2);
		mbCurrentAttrib.putFloatF(0, f1);
	}

	public void data(int i1, int i2, int i3, int i4) {
		if (attribType.clientDataType.bytes != Integer.BYTES) {
			throw new UnsupportedOperationException();
		}

		mbCurrentAttrib.putIntI(3, i4);
		mbCurrentAttrib.putIntI(2, i3);
		mbCurrentAttrib.putIntI(1, i2);
		mbCurrentAttrib.putIntI(0, i1);
	}

	public void endVertex(int vertexIndex) {
		mbCurrentAttrib.copyTo(mb, 0, vertexIndex * growBytes, growBytes);
	}

	public ByteBuffer getBuffer(int vertexCount) {
		return mb.getAsByteBuffer(getSizeInBytes(growBytes));
	}
	
	public int getSizeInBytes(int vertexCount) {
		return vertexCount * growBytes;
	}
	
	public MemoryBlock getCurrentAttribMemoryBlock() {
		return this.mbCurrentAttrib;
	}
	
	public MemoryBlock getMemoryBlock() {
		return this.mb;
	}

	public void growIfNeed(int vertexCount) {
		if (mb.bytes() < vertexCount * growBytes) {
			mb.reallocate(vertexCount * growBytes);
		}
	}

}

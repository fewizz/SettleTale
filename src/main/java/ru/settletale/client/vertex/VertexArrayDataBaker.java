package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

public class VertexArrayDataBaker {
	private final AttribArrayData[] attributes;
	private int vertexCount = 0;
	private int attributeCount = 0;

	public VertexArrayDataBaker(VertexAttribType... attribTypes) {
		this();
		for(int attribIndex = 0; attribIndex < attribTypes.length; attribIndex++) {
			addStorage(attribTypes[attribIndex], attribIndex);
		}
	}
	
	public VertexArrayDataBaker() {
		this.attributes = new AttribArrayData[16];
	}

	public VertexAttribType getAttribType(int index) {
		return attributes[index].attribType;
	}

	public int getCount() {
		return this.attributeCount;
	}

	public VertexArrayDataBaker addStorage(VertexAttribType at, int index) {
		boolean replaces = attributes[index] != null;
		attributes[index] = at.getNewVertexAttribArrayBuffer();

		if (!replaces)
			attributeCount++;
		
		return this;
	}

	public void endVertex() {
		for (int index = 0; index < attributes.length; index++) {
			if (attributes[index] != null)
				attributes[index].dataEnd(vertexCount);
		}

		vertexCount++;
	}

	public ByteBuffer getBuffer(int index) {
		return attributes[index].getBuffer();
	}

	public void clearData() {
		for (int index = 0; index < attributes.length; index++) {
			if (attributes[index] != null)
				attributes[index].clear();
		}
		vertexCount = 0;
	}

	public void delete() {
		clearData();
		
		for (int index = 0; index < attributes.length; index++) {
			if (attributes[index] != null)
				attributes[index].delete();
		}
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	public void putFloat(int index, float f1, float f2, float f3, float f4) {
		attributes[index].data(f1, f2, f3, f4);
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
		attributes[index].data(i1, i2, i3, i4);
	}

	public void putInt(int index, int i1) {
		this.putInt(index, i1, 0, 0, 0);
	}

	public void putShort(int index, short s1) {
		putShort(index, s1, (short) 0, (short) 0, (short) 0);
	}

	public void putShort(int index, short s1, short s2, short s3, short s4) {
		attributes[index].data(s1, s2, s3, s4);
	}

	public void putByte(int index, byte b1, byte b2, byte b3, byte b4) {
		attributes[index].data(b1, b2, b3, b4);
	}

	public void putByte(int index, byte b1) {
		this.putByte(index, b1, (byte) 0, (byte) 0, (byte) 0);
	}
}

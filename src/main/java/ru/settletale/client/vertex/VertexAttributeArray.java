package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

public class VertexAttributeArray {
	private final AttributeStorageAbstarct[] attributeStorages;
	private final AttributeType[] attributes;
	protected int vertexCount = 0;
	private int attributeCount = 0;

	public VertexAttributeArray(AttributeType... storages) {
		this.attributeStorages = new AttributeStorageAbstarct[16];
		this.attributes = new AttributeType[16];
		addAttibutes(storages);
	}

	public void addAttibutes(AttributeType... storages) {
		for (AttributeType storage : storages) {
			addStorage(storage);
		}
	}
	
	public int getStorageCount() {
		return this.attributeCount;
	}
	
	public AttributeStorageAbstarct[] getStorages() {
		return this.attributeStorages;
	}

	protected void addStorage(AttributeType es) {
		attributeStorages[attributeCount] = es.getAttributeStorage();
		attributes[attributeCount] = es;
		attributeCount++;
	}

	public AttributeType getAttribute(int index) {
		return attributes[index];
	}
	
	public void endVertex() {
		for (int i = 0; i < attributeCount; i++) {
			attributeStorages[i].dataEnd(vertexCount);
		}

		vertexCount++;
	}

	public ByteBuffer getBuffer(int storage) {
		return attributeStorages[storage].getBuffer();
	}

	public void clear() {
		for (int i = 0; i < attributeCount; i++) {
			attributeStorages[i].clear();
		}
		vertexCount = 0;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}

	public void data(int storage, float f1, float f2, float f3, float f4) {
		attributeStorages[storage].data(f1, f2, f3, f4);
	}

	public void data(int storage, float f1) {
		this.data(storage, f1, 0, 0, 0);
	}

	public void data(int storage, float f1, float f2, float f3) {
		this.data(storage, f1, f2, f3, 0);
	}

	public void data(int storage, float f1, float f2) {
		this.data(storage, f1, f2, 0, 0);
	}

	public void data(int storage, int i1, int i2, int i3, int i4) {
		attributeStorages[storage].data(i1, i2, i3, i4);
	}

	public void data(int storage, int i1) {
		this.data(storage, i1, 0, 0, 0);
	}

	public void data(int storage, byte b1, byte b2, byte b3, byte b4) {
		attributeStorages[storage].data(b1, b2, b3, b4);
	}

	public void data(int storage, byte b1) {
		this.data(storage, b1, 0, 0, 0);
	}
}

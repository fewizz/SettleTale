package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import ru.settletale.util.Primitive;

public class PrimitiveArray {
	private final VertexStorageAbstarct[] storages;
	private final StorageInfo[] storagesInfos;
	protected int vertexCount = 0;
	private int storageCount = 0;
	
	public PrimitiveArray(StorageInfo... storages) {
		this.storages = new VertexStorageAbstarct[16];
		this.storagesInfos = new StorageInfo[16];
		addStorages(storages);
	}

	public static enum StorageInfo {
		FLOAT_4(new VertexStorageFloat(4)),
		FLOAT_3(new VertexStorageFloat(3)),
		FLOAT_2(new VertexStorageFloat(2)),
		FLOAT_1(new VertexStorageFloat(1)),
		INT_1(new VertexStorageInt(1)),
		BYTE_4(new VertexStorageByte(4)),
		BYTE_3(new VertexStorageByte(3)),
		BYTE_1(new VertexStorageByte(1));
		
		final Primitive primitive;
		final VertexStorageAbstarct vs;
		final int perVertexElementCount;

		private StorageInfo(VertexStorageAbstarct vs) {
			this.vs = vs;
			this.perVertexElementCount = vs.count;
			this.primitive = vs.primitive;
		}
		
		public VertexStorageAbstarct getVertexStorage() {
			return vs;
		}
		
		public int getElementCount() {
			return perVertexElementCount;
		}
		
		public Primitive getPrimitiveType() {
			return primitive;
		}
	}

	public void addStorages(StorageInfo... storages) {
		for(StorageInfo storage : storages) {
			addStorage(storage);
		}
	}
	
	protected void addStorage(StorageInfo es) {
		storages[storageCount] = es.getVertexStorage();
		storagesInfos[storageCount] = es;
		storageCount++;
	}

	public StorageInfo getStorageInfo(int index) {
		return storagesInfos[index];
	}
	
	public void data(int storage, float f1, float f2, float f3, float f4) {
		storages[storage].data(f1, f2, f3, f4);
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
		storages[storage].data(i1, i2, i3, i4);
	}
	
	public void data(int storage, int i1) {
		this.data(storage, i1, 0, 0, 0);
	}

	public void data(int storage, byte b1, byte b2, byte b3, byte b4) {
		storages[storage].data(b1, b2, b3, b4);
	}

	public void data(int storage, byte b1) {
		this.data(storage, b1, 0, 0, 0);
	}

	public void endVertex() {
		for (int i = 0; i < storageCount; i++) {
			storages[i].dataEnd(vertexCount);
		}

		vertexCount++;
	}

	public ByteBuffer getBuffer(int storage) {
		return storages[storage].getBuffer();
	}

	public void clear() {
		for (int i = 0; i < storageCount; i++) {
			storages[i].clear();
		}
		vertexCount = 0;
	}

	public int getVertexCount() {
		return this.vertexCount;
	}
}

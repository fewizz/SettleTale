package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.util.DirectByteBufferUtils;

public class PrimitiveArray {
	private final VertexStorageAbstarct[] storages;
	ByteBuffer ib;
	private int lastVertex = 0;
	private int lastIndex = 0;
	private int lastAddedStorage = 0;

	public PrimitiveArray(Storage... storages) {
		this(false, storages);
	}
	
	public PrimitiveArray(boolean indexed, Storage... storages) {
		if(indexed) {
			ib = MemoryUtil.memAlloc(4096);
			ib.limit(0);
		}

		this.storages = new VertexStorageAbstarct[storages.length];

		for (Storage data : storages) {
			data.setBool(this);
		}
	}

	public static enum Storage {
		FLOAT_4 {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.addStorage(new VertexStorageFloat(4));
			}
		},
		FLOAT_3 {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.addStorage(new VertexStorageFloat(3));
			}
		},
		FLOAT_2 {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.addStorage(new VertexStorageFloat(2));
			}
		},
		FLOAT_1 {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.addStorage(new VertexStorageFloat(1));
			}
		},
		BYTE_4 {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.addStorage(new VertexStorageByte(4));
			}
		},
		BYTE_3 {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.addStorage(new VertexStorageByte(3));
			}
		},
		BYTE_1 {
			@Override
			void setBool(PrimitiveArray arr) {
				arr.addStorage(new VertexStorageByte(1));
			}
		};

		void setBool(PrimitiveArray arr) {
		}
	}

	private void addStorage(VertexStorageAbstarct s) {
		storages[lastAddedStorage++] = s;
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

	public void data(int storage, byte b1, byte b2, byte b3, byte b4) {
		storages[storage].data(b1, b2, b3, b4);
	}

	public void data(int storage, byte b1) {
		this.data(storage, b1, 0, 0, 0);
	}

	public void endVertex() {
		for (VertexStorageAbstarct s : storages) {
			s.dataEnd(lastVertex);
		}

		lastVertex++;
	}

	public void index(int vertexIndex) {
		int id = lastIndex;

		int sizeBytes = 1 * Short.BYTES;
		id *= sizeBytes;

		int limit = id + sizeBytes;

		if (limit > ib.capacity())
			DirectByteBufferUtils.growBuffer(ib, 1.5F);

		ib.limit(Math.max(ib.limit(), limit));

		ib.putShort(id, (short) vertexIndex);

		ib.position(0);

		lastIndex++;
	}

	public ByteBuffer getBuffer(int storage) {
		return storages[storage].getBuffer();
	}

	public ByteBuffer getIndexBuffer() {
		return ib;
	}

	public void clear() {
		for (VertexStorageAbstarct s : storages) {
			s.clear();
		}
		lastVertex = 0;
		lastIndex = 0;
	}

	public int getVertexCount() {
		return this.lastVertex;
	}

	public int getIndexCount() {
		return this.lastIndex;
	}
}

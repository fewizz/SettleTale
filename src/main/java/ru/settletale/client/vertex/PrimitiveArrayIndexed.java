package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.util.DirectByteBufferUtils;

public class PrimitiveArrayIndexed extends PrimitiveArray {
	private ByteBuffer ib;
	private int indexCount = 0;
	
	public PrimitiveArrayIndexed(StorageInfo... storages) {
		super(storages);
		ib = MemoryUtil.memAlloc(4096);
		ib.limit(0);
	}
	
	public void index(int vertexIndex) {
		int id = indexCount;

		int sizeBytes = Short.BYTES;
		id *= sizeBytes;

		int limit = id + sizeBytes;

		if (limit > ib.capacity())
			DirectByteBufferUtils.growBuffer(ib, 1.5F);

		ib.limit(limit);

		ib.putShort(id, (short) vertexIndex);

		ib.position(0);

		indexCount++;
	}
	
	public ByteBuffer getIndexBuffer() {
		return ib;
	}
	
	@Override
	public void clear() {
		super.clear();
		indexCount = 0;
	}
	
	public int getIndexCount() {
		return this.indexCount;
	}
}

package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import ru.settletale.util.DirectByteBufferUtils;

public class VertexColorStorage3Byte implements IVertexColorStorage {
	ByteBuffer cb;
	ByteBuffer ib;
	
	public VertexColorStorage3Byte() {
		cb = BufferUtils.createByteBuffer(4096);
		ib = BufferUtils.createByteBuffer(4096);
		cb.limit(0);
		ib.limit(0);
	}

	@Override
	public void color(byte r, byte g, byte b, byte a, int id) {
		int sizeBytes = 3 * Byte.BYTES;
		id *= sizeBytes;
		
		int limit = id + sizeBytes;
		
		if(limit > cb.capacity())
			cb = DirectByteBufferUtils.grow(cb, 1.5F);
		
		cb.limit(Math.max(cb.limit(), limit));
		
		cb.put(id + 0, r);
		cb.put(id + 1, g);
		cb.put(id + 2, b);
		
		cb.position(0);
	}

	@Override
	public ByteBuffer getBuffer() {
		return cb;
	}
	
	@Override
	public void index(int index, int id) {
		int sizeBytes = 1 * Short.BYTES;
		id *= sizeBytes;
		
		int limit = id + sizeBytes;
		
		if(limit > ib.capacity())
			ib = DirectByteBufferUtils.grow(ib, 1.5F);
		
		ib.limit(Math.max(ib.limit(), limit));
		
		ib.putShort(id, (short) index);
		
		ib.position(0);
	}

	@Override
	public ByteBuffer getIndexBuffer() {
		return ib;
	}

	@Override
	public void clear() {
		cb.clear();
		ib.clear();
	}
}

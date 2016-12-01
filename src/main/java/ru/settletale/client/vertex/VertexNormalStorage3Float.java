package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import ru.settletale.util.DirectByteBufferUtils;

public class VertexNormalStorage3Float implements IVertexNormalStorage {
	ByteBuffer pb;
	ByteBuffer ib;
	
	public VertexNormalStorage3Float() {
		pb = BufferUtils.createByteBuffer(4096);
		ib = BufferUtils.createByteBuffer(4096);
		pb.limit(0);
		ib.limit(0);
	}

	@Override
	public void normal(float x, float y, float z, int id) {
		int sizeBytes = 3 * Float.BYTES;
		id *= sizeBytes;
		
		int limit = id + sizeBytes;
		
		if(limit > pb.capacity())
			pb = DirectByteBufferUtils.grow(pb, 1.5F);
		
		pb.limit(Math.max(pb.limit(), limit));
		
		pb.putFloat(id + Float.BYTES * 0, x);
		pb.putFloat(id + Float.BYTES * 1, y);
		pb.putFloat(id + Float.BYTES * 2, z);
		
		pb.position(0);
	}

	@Override
	public ByteBuffer getBuffer() {
		return pb;
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
		pb.clear();
		ib.clear();
	}

}

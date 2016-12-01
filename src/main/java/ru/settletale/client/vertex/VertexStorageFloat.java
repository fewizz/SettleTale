package ru.settletale.client.vertex;

import ru.settletale.util.DirectByteBufferUtils;

public class VertexStorageFloat extends VertexStorageAbstarct implements IVertexStorage {
	float f1;
	float f2;
	float f3;
	float f4;
	
	public VertexStorageFloat(int size) {
		super(size);
	}

	@Override
	public void data(float f1, float f2, float f3, float f4) {
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		this.f4 = f4;
	}

	@Override
	public void dataEnd(int id) {
		int sizeBytes = size * Float.BYTES;
		id *= sizeBytes;
		
		int limit = id + sizeBytes;
		
		if(limit > buff.capacity())
			buff = DirectByteBufferUtils.grow(buff, 1.5F);
		
		buff.limit(Math.max(buff.limit(), limit));
		
		buff.putFloat(id + Float.BYTES * 0, f1);
		
		switch (size) {
			case 2:
				buff.putFloat(id + Float.BYTES * 1, (float) f2);
				break;
			case 3: 
				buff.putFloat(id + Float.BYTES * 1, (float) f2);
				buff.putFloat(id + Float.BYTES * 2, (float) f3);
				break;
			case 4:
				buff.putFloat(id + Float.BYTES * 1, (float) f2);
				buff.putFloat(id + Float.BYTES * 2, (float) f3);
				buff.putFloat(id + Float.BYTES * 3, (float) f4);
				break;

			default:
				break;
		}
		
		buff.position(0);
	}

}

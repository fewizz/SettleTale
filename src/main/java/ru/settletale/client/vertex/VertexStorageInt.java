package ru.settletale.client.vertex;

import ru.settletale.util.DirectByteBufferUtils;
import ru.settletale.util.Primitive;

public class VertexStorageInt extends VertexStorageAbstarct {
	int i1;
	int i2;
	int i3;
	int i4;
	
	public VertexStorageInt(int size) {
		super(size, Primitive.INT);
	}

	@Override
	public void data(int i1, int i2, int i3, int i4) {
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
		this.i4 = i4;
	}

	@Override
	public void dataEnd(int id) {
		id *= growBytes;
		
		int limit = id + growBytes;
		
		if(limit > buff.capacity())
			DirectByteBufferUtils.growBuffer(buff, 1.5F);
		
		buff.limit(limit);
		
		buff.putInt(id, i1);
		
		switch (count) {
			case 2:
				buff.putInt(id + Integer.BYTES * 1, i2);
				break;
			case 3: 
				buff.putInt(id + Integer.BYTES * 1, i2);
				buff.putInt(id + Integer.BYTES * 2, i3);
				break;
			case 4:
				buff.putInt(id + Integer.BYTES * 1, i2);
				buff.putInt(id + Integer.BYTES * 2, i3);
				buff.putInt(id + Integer.BYTES * 3, i4);
				break;

			default:
				break;
		}
	}
}

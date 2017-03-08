package ru.settletale.client.vertex;

import ru.settletale.util.DirectByteBufferUtils;
import ru.settletale.util.Primitive;

public class AttributeStorageFloat extends AttributeStorageAbstarct {
	float f1;
	float f2;
	float f3;
	float f4;
	
	public AttributeStorageFloat(int size) {
		super(size, Primitive.FLOAT);
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
		id *= growBytes;
		
		int limit = id + growBytes;
		
		if(limit > buff.capacity())
			DirectByteBufferUtils.growBuffer(buff, 1.5F);
		
		buff.limit(limit);
		
		buff.putFloat(id, f1);
		
		switch (count) {
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
	}

}

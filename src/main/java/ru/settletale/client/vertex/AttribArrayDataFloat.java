package ru.settletale.client.vertex;

import ru.settletale.util.DirectBufferUtils;

public class AttribArrayDataFloat extends AttribArrayData {
	float f1;
	float f2;
	float f3;
	float f4;
	
	public AttribArrayDataFloat(VertexAttribType attribType) {
		super(attribType);
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
			buff = DirectBufferUtils.growBuffer(buff, 1.5F);
		
		buff.limit(limit);
		
		buff.putFloat(id, f1);
		
		switch (perVertexElemrntCount) {
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

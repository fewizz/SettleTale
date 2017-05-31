package ru.settletale.client.vertex;

public class AttribArrayDataFloat extends AttribArrayData {
	float f1;
	float f2;
	float f3;
	float f4;
	
	public AttribArrayDataFloat(VertexArrayDataBaker baker, VertexAttribType attribType) {
		super(baker, attribType);
	}

	@Override
	public void data(float f1, float f2, float f3, float f4) {
		this.f1 = f1;
		this.f2 = f2;
		this.f3 = f3;
		this.f4 = f4;
	}

	@Override
	public void dataEnd() {
		int index = baker.lastVertexIndex * growBytes;
		
		int limit = index + growBytes;
		
		buff.limit(limit);
		
		buff.putFloat(index, f1);
		
		switch (attribType.perVertexElementCount) {
			case 2:
				buff.putFloat(index + Float.BYTES * 1, (float) f2);
				break;
			case 3: 
				buff.putFloat(index + Float.BYTES * 1, (float) f2);
				buff.putFloat(index + Float.BYTES * 2, (float) f3);
				break;
			case 4:
				buff.putFloat(index + Float.BYTES * 1, (float) f2);
				buff.putFloat(index + Float.BYTES * 2, (float) f3);
				buff.putFloat(index + Float.BYTES * 3, (float) f4);
				break;

			default:
				break;
		}
	}

}

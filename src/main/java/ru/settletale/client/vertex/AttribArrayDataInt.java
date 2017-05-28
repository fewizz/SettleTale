package ru.settletale.client.vertex;

public class AttribArrayDataInt extends AttribArrayData {
	int i1;
	int i2;
	int i3;
	int i4;
	
	public AttribArrayDataInt(int vertexCount, boolean dynamic, VertexAttribType attribType) {
		super(vertexCount, dynamic, attribType);
	}

	@Override
	public void data(int i1, int i2, int i3, int i4) {
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
		this.i4 = i4;
	}

	@Override
	public void dataEnd(int index) {
		index *= growBytes;
		
		int limit = index + growBytes;
		
		growIfNeed(limit);
		
		buff.limit(limit);
		
		buff.putInt(index, i1);
		
		switch (attribType.perVertexElementCount) {
			case 2:
				buff.putInt(index + Integer.BYTES * 1, i2);
				break;
			case 3: 
				buff.putInt(index + Integer.BYTES * 1, i2);
				buff.putInt(index + Integer.BYTES * 2, i3);
				break;
			case 4:
				buff.putInt(index + Integer.BYTES * 1, i2);
				buff.putInt(index + Integer.BYTES * 2, i3);
				buff.putInt(index + Integer.BYTES * 3, i4);
				break;

			default:
				break;
		}
	}
}

package ru.settletale.client.vertex;

public class AttribArrayDataByte extends AttribArrayData {
	byte b1;
	byte b2;
	byte b3;
	byte b4;
	
	public AttribArrayDataByte(VertexArrayDataBaker baker, VertexAttribType attribType) {
		super(baker, attribType);
	}

	@Override
	public void data(byte b1, byte b2, byte b3, byte b4) {
		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;
		this.b4 = b4;
	}

	@Override
	public void dataEnd() {
		int index = baker.lastVertexIndex * growBytes;
		
		int maxIndex = index + growBytes;
		
		buff.limit(maxIndex);
		
		buff.put(index, b1);
		
		switch (attribType.perVertexElementCount) {
			case 2:
				buff.put(index + 1, b2);
				break;
			case 3: 
				buff.put(index + 1, b2);
				buff.put(index + 2, b3);
				break;
			case 4:
				buff.put(index + 1, b2);
				buff.put(index + 2, b3);
				buff.put(index + 3, b4);
				break;

			default:
				break;
		}
	}
}

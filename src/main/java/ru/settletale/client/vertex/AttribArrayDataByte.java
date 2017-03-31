package ru.settletale.client.vertex;

import ru.settletale.util.DirectByteBufferUtils;

public class AttribArrayDataByte extends AttribArrayData {
	byte b1;
	byte b2;
	byte b3;
	byte b4;
	
	public AttribArrayDataByte(int size, VertexAttribType attribType) {
		super(size, attribType);
	}

	@Override
	public void data(byte b1, byte b2, byte b3, byte b4) {
		this.b1 = b1;
		this.b2 = b2;
		this.b3 = b3;
		this.b4 = b4;
	}

	@Override
	public void dataEnd(int id) {
		id *= growBytes;
		
		int limit = id + growBytes;
		
		if(limit > buff.capacity())
			buff = DirectByteBufferUtils.growBuffer(buff, 1.5F);
		
		buff.limit(limit);
		
		buff.put(id, b1);
		
		switch (perVertexElemrntCount) {
			case 2:
				buff.put(id + 1, b2);
				break;
			case 3: 
				buff.put(id + 1, b2);
				buff.put(id + 2, b3);
				break;
			case 4:
				buff.put(id + 1, b2);
				buff.put(id + 2, b3);
				buff.put(id + 3, b4);
				break;

			default:
				break;
		}
	}
}

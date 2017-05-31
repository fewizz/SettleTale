package ru.settletale.client.vertex;

public class AttribArrayDataShort extends AttribArrayData {
	short s1;
	short s2;
	short s3;
	short s4;

	public AttribArrayDataShort(VertexArrayDataBaker baker, VertexAttribType attribType) {
		super(baker, attribType);
	}

	@Override
	public void dataEnd() {
		
	}

}

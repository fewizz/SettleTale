package ru.settletale.client.vertex;

public class AttribArrayDataShort extends AttribArrayData {
	short s1;
	short s2;
	short s3;
	short s4;

	public AttribArrayDataShort(int vertexCount, boolean dynamic, VertexAttribType attribType) {
		super(vertexCount, dynamic, attribType);
	}

	@Override
	public void dataEnd(int id) {
		
	}

}

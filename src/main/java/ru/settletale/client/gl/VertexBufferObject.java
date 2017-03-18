package ru.settletale.client.gl;

import org.lwjgl.opengl.GL15;

public class VertexBufferObject extends BufferObject<VertexBufferObject> {
	public int attribLocation = -1;
	
	public VertexBufferObject() {
		super(GL15.GL_ARRAY_BUFFER);
	}
}

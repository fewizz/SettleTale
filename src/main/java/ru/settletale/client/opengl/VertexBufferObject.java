package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL15;

public class VertexBufferObject extends BufferObject<VertexBufferObject> {
	
	public VertexBufferObject() {
		super(GL15.GL_ARRAY_BUFFER);
	}
}

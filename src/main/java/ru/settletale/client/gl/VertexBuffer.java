package ru.settletale.client.gl;

import org.lwjgl.opengl.GL15;

public class VertexBuffer extends GLBuffer<VertexBuffer> {
	
	@Override
	public VertexBuffer gen() {
		return super.gen(GL15.GL_ARRAY_BUFFER);
	}
}
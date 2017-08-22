package ru.settletale.client.gl;

import org.lwjgl.opengl.GL31;

public class UniformBuffer extends GLBuffer<UniformBuffer> {
	
	@Override
	public UniformBuffer gen() {
		return super.gen(GL31.GL_UNIFORM_BUFFER);
	}
}

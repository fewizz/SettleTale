package ru.settletale.client.gl;

import org.lwjgl.opengl.GL31;

public class UniformBufferObject extends BufferObject<UniformBufferObject> {

	public UniformBufferObject() {
		super(GL31.GL_UNIFORM_BUFFER);
	}
}

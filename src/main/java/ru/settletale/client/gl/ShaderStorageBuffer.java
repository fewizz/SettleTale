package ru.settletale.client.gl;

import org.lwjgl.opengl.GL43;

public class ShaderStorageBuffer extends GLBuffer<ShaderStorageBuffer> {
	
	@Override
	public ShaderStorageBuffer gen() {
		return super.gen(GL43.GL_SHADER_STORAGE_BUFFER);
	}
}

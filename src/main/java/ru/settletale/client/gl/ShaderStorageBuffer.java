package ru.settletale.client.gl;

import org.lwjgl.opengl.GL43;

public class ShaderStorageBuffer extends GLBuffer<ShaderStorageBuffer> {

	protected ShaderStorageBuffer(int id) {
		super(GL43.GL_SHADER_STORAGE_BUFFER);
	}
}

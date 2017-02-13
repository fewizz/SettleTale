package ru.settletale.client.gl;

import org.lwjgl.opengl.GL43;

public class ShaderStorageBufferObject extends BufferObject<ShaderStorageBufferObject> {

	protected ShaderStorageBufferObject(int id) {
		super(GL43.GL_SHADER_STORAGE_BUFFER);
	}
}

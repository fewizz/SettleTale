package ru.settletale.client.gl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Texture2D extends TextureAbstract<Texture2D> {
	public final int width;
	public final int height;

	public Texture2D(int width, int height) {
		super(GL11.GL_TEXTURE_2D);
		this.width = width;
		this.height = height;
	}

	@Override
	public void loadData(ByteBuffer buffer) {
		bind();
		GL11.glTexImage2D(type, 0, internalFormat, width, height, 0, bufferDataFormat, bufferDataType, buffer);
	}

	@Override
	public void loadSubData(ByteBuffer buffer) {
		bind();
		GL11.glTexSubImage2D(type, 0, 0, 0, width, height, bufferDataFormat, bufferDataType, buffer);
	}

}

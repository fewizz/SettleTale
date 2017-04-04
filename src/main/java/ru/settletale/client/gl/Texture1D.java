package ru.settletale.client.gl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Texture1D extends Texture<Texture1D> {
	public final int width;
	
	public Texture1D(int width) {
		super(GL11.GL_TEXTURE_1D);
		this.width = width;
	}
	
	@Override
	public void data(ByteBuffer buffer) {
		bind();
		GL11.glTexImage1D(type, 0, internalFormat, width, 0, bufferDataFormat, bufferDataType, buffer);
	}

	@Override
	public void subData(ByteBuffer buffer) {
		bind();
		GL11.glTexSubImage1D(type, 0, 0, width, bufferDataFormat, bufferDataType, buffer);
	}
}

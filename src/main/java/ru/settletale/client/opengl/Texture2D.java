package ru.settletale.client.opengl;

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
	protected void internalLoadData() {
		GL11.glTexImage2D(type, 0, internalFormat, width, height, 0, bufferFormat, bufferType, this.buffer);
	}

	@Override
	protected void internalLoadSubData() {
		GL11.glTexSubImage2D(type, 0, 0, 0, width, height, bufferFormat, bufferType, this.buffer);
	}

}

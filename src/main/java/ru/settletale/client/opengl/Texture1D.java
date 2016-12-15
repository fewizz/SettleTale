package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;

public class Texture1D extends Texture<Texture1D> {
	public final int width;
	
	public Texture1D(int width) {
		super(GL11.GL_TEXTURE_1D);
		this.width = width;
	}
	
	@Override
	public void dataInternal() {
		GL11.glTexImage1D(type, 0, internalFormat, width, 0, bufferFormat, bufferType, this.buffer);
	}

	@Override
	public void subDataInternal() {
		GL11.glTexSubImage1D(type, 0, 0, width, bufferFormat, bufferType, this.buffer);
	}
}

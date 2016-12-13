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
		GL11.glTexImage1D(type, 0, GL11.GL_RGBA, width, 0, GL11.GL_RGBA, GL11.GL_FLOAT, this.buffer);
	}

	@Override
	public void subDataInternal() {
		GL11.glTexImage1D(type, 0, GL11.GL_RGBA, width, 0, GL11.GL_RGBA, GL11.GL_FLOAT, this.buffer);
	}

}

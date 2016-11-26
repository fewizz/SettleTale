package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;

public class Texture1D extends Texture<Texture1D> {
	public final int width;
	
	public Texture1D(int width, int height) {
		super(GL11.GL_TEXTURE_1D);
		this.width = width;
	}

}

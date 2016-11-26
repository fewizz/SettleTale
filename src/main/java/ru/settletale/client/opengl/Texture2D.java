package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;

public class Texture2D extends Texture<Texture2D> {
	public final int width;
	public final int height;

	public Texture2D(int width, int height) {
		super(GL11.GL_TEXTURE_2D);
		this.width = width;
		this.height = height;
	}

}

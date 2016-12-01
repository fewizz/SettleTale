package ru.settletale.client.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Texture2D extends Texture<Texture2D> {
	public final int width;
	public final int height;

	public Texture2D(int width, int height) {
		super(GL11.GL_TEXTURE_2D);
		this.width = width;
		this.height = height;
	}

	@Override
	public void data(ByteBuffer buffer) {
		bind();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	}

}

package ru.settletale.client.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

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
		GL11.glTexImage1D(type, 0, GL11.GL_RGBA, width, 0, GL11.GL_RGBA, GL11.GL_FLOAT, buffer);
	}
	
	public void data(FloatBuffer buffer) {
		bind();
		GL11.glTexImage1D(type, 0, GL11.GL_RGBA, width, 0, GL11.GL_RGBA, GL11.GL_FLOAT, buffer);
	}

}

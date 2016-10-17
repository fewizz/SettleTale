package ru.settletale.client.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public class Texture extends NameableAdapter {
	public static int lastID = 0;
	public final int width;
	public final int height;
	
	protected Texture(int id, int width, int height) {
		super(id);
		this.width = width;
		this.height = height;
	}
	
	public static Texture gen(int width, int height) {
		return new Texture(GL11.glGenTextures(), width, height);
	}
	
	public void data(ByteBuffer buffer) {
		boolean wasBound = !bind();
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		if(!wasBound)
			unbind();
	}
	
	enum Type {
		
	}

	@Override
	public void setLastBoundID(int id) {
		lastID = id;
	}

	@Override
	public int getLastBoundID() {
		return lastID;
	}

	@Override
	public void bindInternal() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
	}

	@Override
	public void unbindInternal() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}
}

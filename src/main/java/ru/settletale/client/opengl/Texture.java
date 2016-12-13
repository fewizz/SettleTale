package ru.settletale.client.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public abstract class Texture<T extends Texture<?>> extends NameableAdapter {
	public static int lastID = 0;
	public final int type;
	public ByteBuffer buffer;
	
	public Texture(int type) {
		super(-1);
		this.type = type; 
	}
	
	@SuppressWarnings("unchecked")
	public T gen() {
		this.id = GL11.glGenTextures();
		setDefaultParams();
		return (T) this;
	}
	
	public void buffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}
	
	@SuppressWarnings("unchecked")
	public T data() {
		bind();
		dataInternal();
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T subData() {
		bind();
		dataInternal();
		return (T) this;
	}
	
	abstract protected void dataInternal();
	
	abstract protected void subDataInternal();
	
	@SuppressWarnings("unchecked")
	public T setDefaultParams() {
		bind();
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		return (T) this;
	}
	
	public void delete() {
		GL11.glDeleteTextures(id);
		id = -1;
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
		GL11.glBindTexture(type, id);
	}

	@Override
	public void unbindInternal() {
		GL11.glBindTexture(type, 0);
	}
}

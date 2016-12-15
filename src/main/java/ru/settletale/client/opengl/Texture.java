package ru.settletale.client.opengl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

public abstract class Texture<T extends Texture<?>> extends NameableAdapter {
	public static int lastID = 0;
	public final int type;
	public ByteBuffer buffer;
	public int internalFormat;
	public int bufferFormat;
	public int bufferType;
	
	public Texture(int type) {
		super(-1);
		this.type = type; 
		internalFormat = GL11.GL_RGBA;
		bufferFormat = GL11.GL_RGBA;
		bufferType = GL11.GL_FLOAT;
	}
	
	@SuppressWarnings("unchecked")
	private T getThis() {
		return (T) this;
	}
	
	public T gen() {
		this.id = GL11.glGenTextures();
		setDefaultParams();
		return getThis();
	}
	
	public T buffer(ByteBuffer buffer) {
		this.buffer = buffer;
		return getThis();
	}
	
	public T internalFormat(int internalFormat) {
		this.internalFormat = internalFormat;
		return getThis();
	}
	
	public T bufferFormat(int bufferFormat) {
		this.bufferFormat = bufferFormat;
		return getThis();
	}
	
	public T bufferType(int bufferType) {
		this.bufferType = bufferType;
		return getThis();
	}
	
	public T loadData() {
		bind();
		dataInternal();
		return getThis();
	}
	
	public T loadSubData() {
		bind();
		subDataInternal();
		return getThis();
	}
	
	abstract protected void dataInternal();
	
	abstract protected void subDataInternal();
	
	public T setDefaultParams() {
		bind();
		
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(type, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		return getThis();
	}
	
	public void delete() {
		unbind();
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

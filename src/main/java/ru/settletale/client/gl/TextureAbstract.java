package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;

public abstract class TextureAbstract<T> extends NameableDataContainerAbstract<T> {
	static int lastID = 0;
	public final int type;
	public int internalFormat;
	public int bufferFormat;
	public int bufferType;
	
	public TextureAbstract(int type) {
		this.type = type; 
		internalFormat = GL11.GL_RGBA;
		bufferFormat = GL11.GL_RGBA;
		bufferType = GL11.GL_FLOAT;
	}
	
	@Override
	public T gen() {
		super.gen();
		setDefaultParams();
		return getThis();
	}
	
	@Override
	public int internalGet() {
		return GL11.glGenTextures();
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
	
	@Override
	public boolean bind() {
		GL.activeTextureUnitTexture(this);
		return super.bind();
	}
	
	public void forceBind() {
		bindInternal();
		setLastBoundID(id);
	}
	
	@Override
	public T loadData() {
		bind();
		internalLoadData();
		unbind();
		
		return getThis();
	}
	
	@Override
	public T loadSubData() {
		bind();
		internalLoadSubData();
		unbind();
		
		return getThis();
	}
	
	abstract protected void internalLoadData();
	
	abstract protected void internalLoadSubData();
	
	public T setDefaultParams() {
		parameter(GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		parameter(GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		parameter(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		parameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		return getThis();
	}
	
	public T parameter(int pname, int param) {
		bind();
		GL11.glTexParameteri(type, pname, param);
		return getThis();
	}
	
	public T parameter(int pname, float param) {
		bind();
		GL11.glTexParameterf(type, pname, param);
		return getThis();
	}
	
	public void delete() {
		unbind();
		GL11.glDeleteTextures(id);
		id = -2;
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

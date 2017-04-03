package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;

public abstract class Texture<T> extends NameableDataContainerAbstract<T> {
	public final int type;
	public int internalFormat;
	public int bufferDataFormat;
	public int bufferDataType;
	
	public Texture(int type) {
		this.type = type; 
		internalFormat = GL11.GL_RGBA;
		bufferDataFormat = GL11.GL_RGBA;
		bufferDataType = GL11.GL_UNSIGNED_BYTE;
	}
	
	@Override
	public T gen() {
		super.gen();
		setDefaultParams();
		return getThis();
	}
	
	@Override
	public boolean isBase() {
		return true;
	}
	
	@Override
	public int genInternal() {
		return GL11.glGenTextures();
	}
	
	public T format(int internalFormat) {
		this.internalFormat = internalFormat;
		return getThis();
	}
	
	public T bufferDataFormat(int texelDataFormat) {
		this.bufferDataFormat = texelDataFormat;
		return getThis();
	}
	
	public T bufferDataType(int bufferDataType) {
		this.bufferDataType = bufferDataType;
		return getThis();
	}
	
	@Override
	public boolean bind() {
		GL.setActiveTextureUnitTexture(this);
		return super.bind();
	}
	
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
	
	@Override
	public void deleteInternal() {
		GL11.glDeleteTextures(id);
		GL.onTextureDeleted(this);
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

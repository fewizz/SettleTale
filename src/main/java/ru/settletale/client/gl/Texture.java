package ru.settletale.client.gl;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public abstract class Texture<T> extends GLBindableObject<T> {
	public final int type;
	public int internalFormat;
	public int bufferDataFormat;
	public int bufferDataType;

	public Texture(int type) {
		this.type = type;
		internalFormat = GL_RGBA;
		bufferDataFormat = GL_RGBA;
		bufferDataType = GL_UNSIGNED_BYTE;
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

	abstract void data(ByteBuffer buffer);

	abstract void subData(ByteBuffer buffer);

	@Override
	public int genInternal() {
		return glGenTextures();
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
	public void bind() {
		GL.setActiveTextureUnitTexture(this);
		super.bind();
	}

	public T setDefaultParams() {
		parameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
		parameter(GL_TEXTURE_WRAP_T, GL_REPEAT);
		parameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		parameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);

		return getThis();
	}

	public T parameter(int pname, int param) {
		bind();
		glTexParameteri(type, pname, param);
		return getThis();
	}

	public T parameter(int pname, float param) {
		bind();
		glTexParameterf(type, pname, param);
		return getThis();
	}

	@Override
	public void deleteInternal() {
		glDeleteTextures(id);
		GL.onTextureDeleted(this);
	}

	@Override
	public void bindInternal() {
		glBindTexture(type, id);
	}

	@Override
	public void unbindInternal() {
		glBindTexture(type, 0);
	}
}

package ru.settletale.client.gl;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public abstract class Texture<T> extends GLBindableObject<T> {
	public static final Texture<Texture<?>> DEFAULT = new Texture<Texture<?>>(GL_TEXTURE_2D) {
		@Override
		public Texture<?> gen() {
			id = 0;
			return getThis();
		}

		@Override
		public void delete() {
			throw new Error();
		}

		@Override
		void data(ByteBuffer buffer) {
			throw new Error();
		}

		@Override
		void subData(ByteBuffer buffer) {
			throw new Error();
		}
	};
	static {
		DEFAULT.gen();
	}

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
	protected int genInternal() {
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
		if (!isGenerated()) {
			throw new Error("Texture is not generated!");
		}
		GL.setActiveTextureUnitTexture(this);

		if (getLastGlobalID() == id) {
			return;
		}
		bindInternal();
		setLastGlobalID(id);
	}

	public void bindWithForce() {
		if (!isGenerated()) {
			throw new Error("Texture is not generated!");
		}
		GL.setActiveTextureUnitTexture(this);
		bindInternal();
		setLastGlobalID(id);
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
	protected void deleteInternal() {
		GL.onTextureDeleted(this);
		glDeleteTextures(id);
	}

	@Override
	protected void bindInternal() {
		glBindTexture(type, id);
	}
}

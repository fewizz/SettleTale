package ru.settletale.client.gl;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import ru.settletale.memory.MemoryBlock;

public class Texture extends GLBindableObject<Texture> {
	public static final Texture DEFAULT = new Texture() {
		@Override
		public Texture gen(int target) {
			throw new Error();
		}
		
		@Override
		public int getID() {
			return 0;
		}
		
		@Override
		public void delete() {
			throw new Error();
		}
	};

	int target = -1;
	
	public Texture() {
		super(Texture.class);
	}
	
	public Texture gen(int target) {
		this.target = target;
		super.gen();
		setDefaultParams();
		return getThis();
	}

	@Override
	protected int genInternal() {
		return glGenTextures();
	}
	
	public void data1D(MemoryBlock buffer, int internalFormat, int width, int bufferDataFormat, int bufferDataType) {
		bind();
		GL11.nglTexImage1D(target, 0, internalFormat, width, 0, bufferDataFormat, bufferDataType, buffer.address());
	}

	public void subData1D(MemoryBlock buffer, int width, int bufferDataFormat, int bufferDataType) {
		bind();
		GL11.nglTexSubImage1D(target, 0, 0, width, bufferDataFormat, bufferDataType, buffer.address());
	}
	
	public void data2D(MemoryBlock buffer, int level, int internalFormat, int width, int height, int bufferDataFormat, int bufferDataType) {
		bind();
		GL11.nglTexImage2D(target, level, internalFormat, width, height, 0, bufferDataFormat, bufferDataType, buffer.address());
	}

	public void subData2D(MemoryBlock buffer, int xOff, int yOff, int zOff, int width, int height, int bufferDataFormat, int bufferDataType) {
		bind();
		GL11.nglTexSubImage2D(target, xOff, yOff, zOff, width, height, bufferDataFormat, bufferDataType, buffer.address());
	}
	
	public void data3D(MemoryBlock buffer, int level, int internalFormat, int width, int height, int depth, int bufferDataFormat, int bufferDataType) {
		bind();
		GL12.glTexImage3D(target, level, internalFormat, width, height, depth, 0, bufferDataFormat, bufferDataType, buffer.address());
	}

	public void subData3D(MemoryBlock buffer, int level, int xOff, int yOff, int zOff, int width, int height, int depth, int bufferDataFormat, int bufferDataType) {
		bind();
		GL12.glTexSubImage3D(target, level, xOff, yOff, zOff, width, height, depth, bufferDataFormat, bufferDataType, buffer.address());
	}

	@Override
	public void bind() {
		if (!isGenerated()) {
			throw new Error("Texture is not generated!");
		}
		GL.setActiveTextureUnitTexture(this);

		if (globalID.get() == getID()) {
			return;
		}
		bindInternal();
		globalID.set(getID());
	}

	public void bindWithForce() {
		if (!isGenerated()) {
			throw new Error("Texture is not generated!");
		}
		GL.setActiveTextureUnitTexture(this);
		bindInternal();
		globalID.set(getID());
	}

	public Texture setDefaultParams() {
		parameter(GL_TEXTURE_WRAP_S, GL_REPEAT);
		parameter(GL_TEXTURE_WRAP_T, GL_REPEAT);
		parameter(GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		parameter(GL_TEXTURE_MIN_FILTER, GL_NEAREST);

		return getThis();
	}

	public Texture parameter(int pname, int param) {
		bind();
		glTexParameteri(target, pname, param);
		return getThis();
	}

	public Texture parameter(int pname, float param) {
		bind();
		glTexParameterf(target, pname, param);
		return getThis();
	}
	
	public int width() {
		bind();
		return glGetTexLevelParameteri(target, 0, GL_TEXTURE_WIDTH);
	}
	
	public int height() {
		bind();
		return glGetTexLevelParameteri(target, 0, GL_TEXTURE_WIDTH);
	}
	
	public int depth() {
		bind();
		return glGetTexLevelParameteri(target, 0, GL12.GL_TEXTURE_DEPTH);
	}

	@Override
	protected void deleteInternal() {
		GL.onTextureDeleted(this);
		glDeleteTextures(getID());
	}

	@Override
	protected void bindInternal() {
		glBindTexture(target, getID());
	}
}

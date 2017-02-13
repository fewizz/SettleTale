package ru.settletale.client.gl;

import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

public class Texture2DArray extends TextureAbstract<Texture2DArray> {
	public final int width;
	public final int height;
	public final int depth;
	public int zOffset;

	public Texture2DArray(int width, int height, int depth) {
		super(GL30.GL_TEXTURE_2D_ARRAY);
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public Texture2DArray zOffset(int zOffset) {
		this.zOffset = zOffset;
		return this;
	}

	@Override
	protected void internalLoadData() {
		GL12.glTexImage3D(type, 0, internalFormat, width, height, depth, 0, bufferFormat, bufferType, this.buffer);
	}

	@Override
	protected void internalLoadSubData() {
		GL12.glTexSubImage3D(type, 0, 0, 0, zOffset, width, height, depth, bufferFormat, bufferType, this.buffer);
	}

}

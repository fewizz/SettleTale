package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;

import ru.settletale.memory.MemoryBlock;

public class Texture2D extends Texture {
	
	@Override
	public Texture2D gen() {
		return (Texture2D) super.gen(GL11.GL_TEXTURE_2D);
	}
	
	@Deprecated
	@Override
	public Texture gen(int target) {
		return super.gen(target);
	}
	
	@Deprecated
	@Override
	public void data1D(MemoryBlock buffer, int internalFormat, int width, int bufferDataFormat, int bufferDataType) {
		super.data1D(buffer, internalFormat, width, bufferDataFormat, bufferDataType);
	}
	
	@Deprecated
	@Override
	public void subData1D(MemoryBlock buffer, int width, int bufferDataFormat, int bufferDataType) {
		super.subData1D(buffer, width, bufferDataFormat, bufferDataType);
	}
	
	@Deprecated
	@Override
	public void data3D(MemoryBlock buffer, int level, int internalFormat, int width, int height, int depth, int bufferDataFormat, int bufferDataType) {
		super.data3D(buffer, level, internalFormat, width, height, depth, bufferDataFormat, bufferDataType);
	}
	
	@Deprecated
	@Override
	public void subData3D(MemoryBlock buffer, int level, int xOff, int yOff, int zOff, int width, int height, int depth, int bufferDataFormat, int bufferDataType) {
		super.subData3D(buffer, level, xOff, yOff, zOff, width, height, depth, bufferDataFormat, bufferDataType);
	}

}

package ru.settletale.client.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

public class GLBuffer<SELF> extends GLBindableObject<SELF> {
	protected int type;
	private int loadedSize = 0;
	private BufferUsage usage;
	private int offset = 0;
	private int storageFlags = GL44.GL_DYNAMIC_STORAGE_BIT;
	
	public GLBuffer() {
		super(GLBuffer.class);
		usage = BufferUsage.STATIC_DRAW;
	}
	
	public SELF gen(int target) {
		this.type = target;
		return super.gen();
	}
	
	@Override
	public int genInternal() {
		return GL.version >= 45 ? GL45.glCreateBuffers() : GL15.glGenBuffers();
	}
	
	public SELF usage(BufferUsage usage) {
		this.usage = usage;
		return getThis();
	}
	
	public SELF offset(int offset) {
		this.offset = offset;
		return getThis();
	}

	@Override
	public void bindInternal() {
		GL15.glBindBuffer(type, getID());
	}
	
	public void storageFlags(int flags) {
		this.storageFlags = flags;
	}
	
	public void storage(ByteBuffer buffer) {
		loadedSize = buffer.remaining();
		
		if(GL.version >= 45) {
			GL45.glNamedBufferStorage(getID(), buffer, storageFlags);
		}
		else {
			bind();
			GL44.glBufferStorage(type, buffer, storageFlags);
		}
	}
	
	public void data(long address, int size) {
		loadedSize = size;
		
		if(GL.version >= 45) {
			GL45.nglNamedBufferData(getID(), size, address, usage.glCode);
		}
		else {
			bind();
			GL15.nglBufferData(type, size, address, usage.glCode);
		}
	}
	
	public void data(ByteBuffer buffer) {
		data(MemoryUtil.memAddress(buffer), buffer.remaining());
	}
	
	public void data(FloatBuffer buffer) {
		this.data(MemoryUtil.memAddress(buffer), buffer.remaining() * Float.BYTES);
	}
	
	public void subData(long address, int size) {
		if(GL.version >= 45) {
			GL45.nglNamedBufferSubData(getID(), offset, size, address);
		}
		else {
			bind();
			GL15.nglBufferSubData(type, offset, size, address);
		}
	}
	
	public void subData(ByteBuffer buffer) {
		this.subData(MemoryUtil.memAddress(buffer), buffer.remaining());
	}
	
	public void subData(FloatBuffer buffer) {
		this.subData(MemoryUtil.memAddress(buffer), buffer.remaining() * Float.BYTES);
	}
	
	public SELF dataOrSubData(long address, int size) {
		if(size <= loadedSize) {
			subData(address, size);
		}
		else {
			data(address, size);
		}
		
		return getThis();
	}
	
	public SELF dataOrSubData(ByteBuffer buffer) {
		return dataOrSubData(MemoryUtil.memAddress(buffer), buffer.remaining());
	}
	
	@Override
	public void delete() {
		super.delete();
		this.loadedSize = 0;
	}
	
	@Override
	public void deleteInternal() {
		GL15.glDeleteBuffers(getID());
	}
	
	public enum BufferUsage {
		STATIC_DRAW(GL15.GL_STATIC_DRAW),
		DYNAMIC_DRAW(GL15.GL_DYNAMIC_DRAW);
		
		final int glCode;
		
		BufferUsage(int glCode) {
			this.glCode = glCode;
		}
	}
}

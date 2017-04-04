package ru.settletale.client.gl;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GL45;

public class BufferObject<T> extends GLObject<T> implements IData, ISubData {
	protected final int type;
	private int loadedSize = 0;
	private Usage usage;
	private int offset = 0;
	private int storageFlags = GL44.GL_DYNAMIC_STORAGE_BIT;
	
	public BufferObject(int type) {
		this.type = type;
		usage = Usage.STATIC_DRAW;
	}
	
	@Override
	public boolean isBase() {
		return true;
	}
	
	@Override
	public int genInternal() {
		return GL.version >= 45 ? GL45.glCreateBuffers() : GL15.glGenBuffers();
	}
	
	public T usage(Usage usage) {
		this.usage = usage;
		return getThis();
	}
	
	public T offset(int offset) {
		this.offset = offset;
		return getThis();
	}

	@Override
	public void bindInternal() {
		GL15.glBindBuffer(type, id);
	}

	@Override
	public void unbindInternal() {
		GL15.glBindBuffer(type, 0);
	}
	
	public void storageFlags(int flags) {
		this.storageFlags = flags;
	}
	
	public void storage(ByteBuffer buffer) {
		loadedSize = buffer.remaining();
		
		if(GL.version >= 45) {
			GL45.glNamedBufferStorage(id, buffer, storageFlags);
		}
		else {
			bind();
			GL44.glBufferStorage(type, buffer, storageFlags);
		}
	}
	
	@Override
	public void data(ByteBuffer buffer) {
		loadedSize = buffer.remaining();
		
		if(GL.version >= 45) {
			GL45.glNamedBufferData(id, buffer, usage.glCode);
		}
		else {
			bind();
			GL15.glBufferData(type, buffer, usage.glCode);
		}
	}

	@Override
	public void subData(ByteBuffer buffer) {
		if(GL.version >= 45) {
			GL45.glNamedBufferSubData(id, offset, buffer);
		}
		else {
			bind();
			GL15.glBufferSubData(type, offset, buffer);
		}
	}
	
	public T loadDataOrSubData(ByteBuffer buffer) {
		int size = buffer.remaining();
		
		if(size <= loadedSize) {
			subData(buffer);
		}
		else {
			data(buffer);
		}
		
		return getThis();
	}
	
	@Override
	public void delete() {
		super.delete();
		this.loadedSize = 0;
	}
	
	@Override
	public void deleteInternal() {
		GL15.glDeleteBuffers(id);
	}
	
	public enum Usage {
		STATIC_DRAW(GL15.GL_STATIC_DRAW),
		DYNAMIC_DRAW(GL15.GL_DYNAMIC_DRAW);
		
		final int glCode;
		
		Usage(int glCode) {
			this.glCode = glCode;
		}
	}
}

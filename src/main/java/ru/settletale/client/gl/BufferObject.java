package ru.settletale.client.gl;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;

public class BufferObject<T> extends NameableDataContainerAbstract<T> {
	protected int type;
	int loadedSize;
	Usage usage;
	int offset;
	
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
	
	public T loadData() {
		loadedSize = buffer.remaining();
		
		if(GL.version >= 45) {
			GL45.glNamedBufferData(id, buffer, usage.glCode);
			return getThis();
		}

		bind();
		GL15.glBufferData(type, buffer, usage.glCode);
		return getThis();
	}

	public T loadSubData() {
		if(GL.version >= 45) {
			GL45.glNamedBufferSubData(id, offset, buffer);
			return getThis();
		}

		bind();
		GL15.glBufferSubData(type, offset, buffer);
		return getThis();
	}
	
	public T loadDataOrSubData() {
		int size = buffer.remaining();
		
		if(size <= loadedSize) {
			loadSubData();
		}
		else {
			loadData();
		}
		
		return getThis();
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

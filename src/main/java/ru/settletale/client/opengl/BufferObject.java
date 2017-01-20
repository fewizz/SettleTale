package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;

public class BufferObject<T> extends NameableDataContainerAbstract<T> {
	static int lastID = -1;
	protected int type;
	Usage usage;
	int offset;
	
	public BufferObject(int type) {
		this.type = type;
		usage = Usage.STATIC_DRAW;
	}
	
	@Override
	public int internalGet() {
		return GL.version >= 45 ? GL45.glCreateBuffers() : GL15.glGenBuffers();
	}
	
	@Override
	public void setLastBoundID(int id) {
		lastID = id;
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
	public int getLastBoundID() {
		return lastID;
	}

	@Override
	public void bindInternal() {
		GL15.glBindBuffer(type, id);
	}

	@Override
	public void unbindInternal() {
		GL15.glBindBuffer(type, 0);
	}
	
	@Override
	public T loadData() {
		if(GL.version >= 45) {
			GL45.glNamedBufferData(id, buffer, usage.glCode);
			return getThis();
		}

		bind();
		GL15.glBufferData(type, buffer, usage.glCode);
		return getThis();
	}

	@Override
	public T loadSubData() {
		if(GL.version >= 45) {
			GL45.glNamedBufferSubData(id, offset, buffer);
			return getThis();
		}

		bind();
		GL15.glBufferSubData(type, offset, buffer);
		return getThis();
	}
	
	public void delete() {
		GL15.glDeleteBuffers(id);
		id = -1;
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

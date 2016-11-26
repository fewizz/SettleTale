package ru.settletale.client.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL45;

public class BufferObject<T extends BufferObject<?>> extends NameableAdapter {
	static int lastID = -1;
	protected int type;
	
	protected static int createBufferName() {
		return GL.version >= 45 ? GL45.glCreateBuffers() : GL15.glGenBuffers();
	}
	
	public BufferObject(int type) {
		super(-1);
		this.type = type;
	}
	
	@SuppressWarnings("unchecked")
	public T gen() {
		id = createBufferName();
		return (T) this;
	}
	
	@Override
	public void setLastBoundID(int id) {
		lastID = id;
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
	
	public void data(FloatBuffer buffer, Usage usage) {
		if(GL.version >= 45) {
			GL45.glNamedBufferData(id, buffer, usage.glCode);
			return;
		}	

		bind();
		GL15.glBufferData(type, buffer, usage.glCode);
	}
	
	public void data(ByteBuffer buffer, Usage usage) {
		if(GL.version >= 45) {
			GL45.glNamedBufferData(id, buffer, usage.glCode);
			return;
		}
		
		bind();
		GL15.glBufferData(type, buffer, usage.glCode);
	}
	
	public void subdata(FloatBuffer buffer, int offset) {
		if(GL.version >= 45) {
			GL45.glNamedBufferSubData(id, offset, buffer);
			return;
		}

		bind();
		GL15.glBufferSubData(type, offset, buffer);
	}
	
	public void subdata(ByteBuffer buffer, int offset) {
		if(GL.version >= 45) {
			GL45.glNamedBufferSubData(id, offset, buffer);
			return;
		}
		
		bind();
		GL15.glBufferSubData(type, offset, buffer);
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

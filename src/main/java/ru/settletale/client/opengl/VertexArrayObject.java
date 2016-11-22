package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

public class VertexArrayObject extends NameableAdapter {
	public static int lastID = 0;
	
	public VertexArrayObject() {
		super(-1);
	}
	
	public VertexArrayObject gen() {
		id = GL30.glGenVertexArrays();
		return this;
	}
	
	public void vertexAttribPointer(BufferObject<?> buffer, int index, int size, int type, boolean normalized, int stride) {
		bind();
		buffer.bind();
		GL20.glVertexAttribPointer(index, size, type, normalized, stride, MemoryUtil.NULL);
	}
	
	public void enableVertexAttribArray(int index) {
		if(GL.version >= 45) {
			GL45.glEnableVertexArrayAttrib(id, index);
			return;
		}
		bind();
		GL20.glEnableVertexAttribArray(0);
	}
	
	public void delete() {
		GL30.glDeleteVertexArrays(id);
		id = -1;
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
		GL30.glBindVertexArray(id);
	}

	@Override
	public void unbindInternal() {
		GL30.glBindVertexArray(0);
	}
}

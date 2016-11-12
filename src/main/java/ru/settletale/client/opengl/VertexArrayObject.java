package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL30;

public class VertexArrayObject extends NameableAdapter {
	public static int lastID = 0;
	
	public VertexArrayObject() {
		super(-1);
	}
	
	public VertexArrayObject gen() {
		id = GL30.glGenVertexArrays();
		return this;
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

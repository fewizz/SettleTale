package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

public class VertexArrayObject extends NameableAbstract<VertexArrayObject> {
	
	@Override
	public int genInternal() {
		return GL30.glGenVertexArrays();
	}
	
	@Override
	public boolean isBase() {
		return true;
	}
	
	public void vertexAttribPointer(BufferObject<?> buffer, int index, int size) {
		vertexAttribPointer(buffer, index, size, GL11.GL_FLOAT, false, 0);
	}
	
	public void vertexAttribPointer(BufferObject<?> buffer, int index, int size, int type) {
		vertexAttribPointer(buffer, index, size, type, false, 0);
	}
	
	public void vertexAttribPointer(BufferObject<?> buffer, int index, int size, int type, boolean normalized) {
		vertexAttribPointer(buffer, index, size, type, normalized, 0);
	}
	
	public void vertexAttribPointer(BufferObject<?> buffer, int index, int size, int type, boolean normalized, int stride) {
		bind();
		buffer.bind();
		GL20.glVertexAttribPointer(index, size, type, normalized, stride, MemoryUtil.NULL);
	}
	
	public void vertexAttribIntPointer(BufferObject<?> buffer, int index, int size) {
		vertexAttribIntPointer(buffer, index, size, GL11.GL_INT, 0);
	}
	
	public void vertexAttribIntPointer(BufferObject<?> buffer, int index, int size, int type) {
		vertexAttribIntPointer(buffer, index, size, type, 0);
	}
	
	public void vertexAttribIntPointer(BufferObject<?> buffer, int index, int size, int type, int stride) {
		bind();
		buffer.bind();
		GL30.glVertexAttribIPointer(index, size, type, stride, MemoryUtil.NULL);
	}
	
	public void enableVertexAttribArray(int index) {
		if(GL.version >= 45) {
			GL45.glEnableVertexArrayAttrib(id, index);
			return;
		}
		bind();
		GL20.glEnableVertexAttribArray(index);
	}
	
	public void disableVertexAttribArray(int index) {
		if(GL.version >= 45) {
			GL45.glDisableVertexArrayAttrib(id, index);
			return;
		}
		bind();
		GL20.glDisableVertexAttribArray(index);
	}
	
	@Override
	public void deleteInternal() {
		GL30.glDeleteVertexArrays(id);
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

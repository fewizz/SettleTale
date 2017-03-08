package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.VertexArrayObject;
import ru.settletale.client.gl.VertexBufferObject;
import ru.settletale.client.vertex.VertexArray;
import ru.settletale.client.vertex.VertexArray.AttributeType;
import ru.settletale.util.ClientUtils;

public class RenderLayer {
	int vertexCount;
	VertexArray vertexArray;
	VertexArrayObject vao;
	ShaderProgram program;
	VertexBufferObject[] buffers;
	int bufferCount;

	public RenderLayer(AttributeType... storages) {
		this(new VertexArray(storages));
	}

	public RenderLayer(VertexArray va) {
		this();
		setVertexArray(va);
	}

	public RenderLayer() {
		buffers = new VertexBufferObject[16];
		vao = new VertexArrayObject();
	}

	public void setVertexArray(VertexArray va) {
		this.vertexArray = va;
	}

	public VertexArray getVertexArray() {
		return this.vertexArray;
	}
	
	public boolean hasVertexArray() {
		return this.vertexArray != null;
	}

	public void compile() {
		compile(false);
	}

	public void compile(boolean allowSubDataIfPossible) {
		if (!vao.isGenerated()) {
			vao.gen();
		}
		vao.bind();

		GL.debug("RenderLayer vao creating");

		this.bufferCount = vertexArray.getStorageCount();
		this.vertexCount = vertexArray.getVertexCount();

		for (int i = 0; i < bufferCount; i++) {
			VertexBufferObject vbo = buffers[i];

			if (vbo == null) {
				vbo = new VertexBufferObject();
				vbo.gen();
				buffers[i] = vbo;
			}

			vbo.buffer(vertexArray.getBuffer(i));

			if (allowSubDataIfPossible) {
				vbo.loadDataOrSubData();
			}
			else {
				vbo.loadData();
			}

			GL.debug("RenderLayer loadData");

			AttributeType si = vertexArray.getAttribute(i);

			vao.vertexAttribPointer(vbo, i, si.getElementCount(), ClientUtils.getGLPrimitive(si.getPrimitiveType()), si.isNormalised());
			vao.enableVertexAttribArray(i);
		}
	}

	public void render(int mode) {
		program.bind();
		vao.bind();

		GL11.glDrawArrays(mode, 0, vertexCount);
	}

	public void setShaderProgram(ShaderProgram program) {
		this.program = program;
	}

	public void delete() {
		for (int i = 0; i < bufferCount; i++) {
			buffers[i].delete();
		}

		bufferCount = 0;

		vao.delete();
	}
}

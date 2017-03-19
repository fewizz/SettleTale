package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.VertexArrayObject;
import ru.settletale.client.gl.VertexBufferObject;
import ru.settletale.client.vertex.AttributeType;
import ru.settletale.client.vertex.VertexAttributeArray;

public class RenderLayer {
	int vertexCount;
	VertexAttributeArray vertexArray;
	VertexArrayObject vao;
	ShaderProgram program;
	VertexBufferObject[] buffers;
	int bufferCount;

	public RenderLayer(AttributeType... storages) {
		this(new VertexAttributeArray(storages));
	}

	public RenderLayer(VertexAttributeArray va) {
		this();
		setVertexArray(va);
	}

	public RenderLayer() {
		buffers = new VertexBufferObject[16];
		vao = new VertexArrayObject();
	}

	public void setVertexArray(VertexAttributeArray va) {
		this.vertexArray = va;
	}

	public VertexAttributeArray getVertexAttributeArray() {
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

			if(si.getAttributeDataType().isIntegral()) {
				vao.vertexAttribIntPointer(vbo, i, si.getPerVertexElementCount(), GL.getGLPrimitiveType(si.getDataType()));
			}
			else {
				vao.vertexAttribPointer(vbo, i, si.getPerVertexElementCount(), GL.getGLPrimitiveType(si.getDataType()), si.isNormalised());
			}
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

	public void clearVertexAttributeArrayIdExists() {
		if(getVertexAttributeArray() != null) {
			getVertexAttributeArray().clear();
		}
	}
	
	public void deleteVertexBuffers() {
		for (int i = 0; i < bufferCount; i++) {
			buffers[i].delete();
		}
		bufferCount = 0;
	}

	public void delete() {
		deleteVertexBuffers();

		vao.delete();
	}
}

package ru.settletale.client.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.VertexArrayObject;
import ru.settletale.client.gl.VertexBufferObject;
import ru.settletale.client.vertex.VertexAttribType;
import ru.settletale.client.vertex.VertexArrayDataBaker;

public class RenderLayer {
	protected VertexArrayDataBaker attribs;
	protected VertexArrayObject vao;
	protected ShaderProgram program;
	protected VertexBufferObject[] vboArray;
	protected boolean allowSubData = false;
	private int bufferCount;
	protected int vertexCount;

	public RenderLayer(VertexAttribType... storages) {
		this();
		setVertexArrayDataBaker(new VertexArrayDataBaker(storages));
	}

	public RenderLayer(VertexArrayDataBaker va) {
		this();
		setVertexArrayDataBaker(va);
	}

	public RenderLayer() {
		vboArray = new VertexBufferObject[16];
		vao = new VertexArrayObject();
	}

	public void compile() {
		if (!vao.isGenerated())
			vao.gen();

		vao.bind();

		GL.debug("RenderLayer vao creating");

		this.bufferCount = attribs.getCount();
		this.vertexCount = attribs.getVertexCount();

		for (int attribIndex = 0; attribIndex < bufferCount; attribIndex++) {
			VertexBufferObject vbo = vboArray[attribIndex];

			if (vbo == null) {
				vbo = new VertexBufferObject().gen();
				vboArray[attribIndex] = vbo;
			}

			ByteBuffer buffer = attribs.getBuffer(attribIndex);

			if (allowSubData)
				vbo.loadDataOrSubData(buffer);
			else
				vbo.loadData(buffer);

			GL.debug("RenderLayer loadData");

			vao.bindAttribPointer(vbo, attribIndex, attribs.getAttribType(attribIndex));
			GL.debug("Bind buffers to vao");
		}
	}

	public void render(int mode) {
		program.bind();
		vao.bind();

		GL11.glDrawArrays(mode, 0, vertexCount);
	}

	public void setVertexArrayDataBaker(VertexArrayDataBaker va) {
		this.attribs = va;
	}
	
	public void setAllowSubData(boolean allowSubData) {
		this.allowSubData = allowSubData;
	}

	public VertexArrayDataBaker getVertexArrayDataBaker() {
		return this.attribs;
	}

	public boolean hasVertexAttribArray() {
		return this.attribs != null;
	}

	public void setShaderProgram(ShaderProgram program) {
		this.program = program;
	}

	public void clearVertexAttribArrayIfExists() {
		if (getVertexArrayDataBaker() != null)
			getVertexArrayDataBaker().clearData();
	}

	public void deleteVertexBuffers() {
		for (int i = 0; i < bufferCount; i++) {
			vboArray[i].delete();
		}
		bufferCount = 0;
	}

	public void delete() {
		deleteVertexBuffers();
		vao.delete();
	}
}

package ru.settletale.client.render;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.VertexArray;
import ru.settletale.client.gl.VertexBuffer;
import ru.settletale.client.vertex.VertexAttribType;
import ru.settletale.client.vertex.VertexArrayDataBaker;

public class RenderLayer {
	protected VertexArrayDataBaker attribs;
	protected final VertexArray vao;
	protected ShaderProgram program;
	protected final List<VertexBuffer> vboList;
	protected boolean allowSubData = false;
	protected int vertexCount;

	public RenderLayer(int vertexCount, VertexAttribType... storages) {
		this();
		this.vertexCount = vertexCount;
		setVertexArrayDataBaker(new VertexArrayDataBaker(vertexCount, false, storages));
	}
	
	public RenderLayer(VertexAttribType... storages) {
		this();
		setVertexArrayDataBaker(new VertexArrayDataBaker(1024, true, storages));
	}

	public RenderLayer(VertexArrayDataBaker va) {
		this();
		setVertexArrayDataBaker(va);
	}

	public RenderLayer() {
		vboList = new ArrayList<>();
		vao = new VertexArray();
	}

	public void compile() {
		if (!vao.isGenerated())
			vao.gen();

		vao.bind();

		GL.debug("RenderLayer vao creating");

		this.vertexCount = attribs.getVertexCount();
		
		for(int index = 0; index < attribs.getAttributeCount(); index++) {
			if(vboList.size() <= index || vboList.get(index) == null) {
				vboList.add(new VertexBuffer().gen());
			}
		}

		for (int attribIndex = 0; attribIndex < attribs.getAttributeCount(); attribIndex++) {
			VertexBuffer vbo = vboList.get(attribIndex);
			ByteBuffer buffer = attribs.getBuffer(attribIndex);

			if (allowSubData)
				vbo.loadDataOrSubData(buffer);
			else
				vbo.data(buffer);

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
	
	public void setAllowSubDataWhenPossible(boolean allowSubData) {
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

	public void clearVertexArrayDataBakerIfExists() {
		if (hasVertexAttribArray())
			getVertexArrayDataBaker().clearData();
	}

	public void deleteVertexBuffers() {
		vboList.forEach(vbo -> vbo.delete());
	}

	public void delete() {
		deleteVertexBuffers();
		vao.delete();
	}
}

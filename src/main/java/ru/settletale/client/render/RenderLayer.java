package ru.settletale.client.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.VertexArray;
import ru.settletale.client.gl.VertexBuffer;
import ru.settletale.client.render.vertex.AttribArrayData;
import ru.settletale.client.render.vertex.VertexArrayDataBaker;

public class RenderLayer {
	protected VertexArrayDataBaker attribs;
	protected final VertexArray vao;
	public ShaderProgram program;
	protected final IntObjMap<VertexBuffer> vbos;
	protected boolean allowSubData = false;
	protected int vertexCount;
	protected ITextureBinder textureBinder;

	public RenderLayer(VertexArrayDataBaker va) {
		this();
		setVertexArrayDataBaker(va);
	}

	public RenderLayer() {
		vbos = HashIntObjMaps.newMutableMap(0);
		vao = new VertexArray();
	}

	public void compile() {
		if (!vao.isGenerated())
			vao.gen();

		vao.bind();

		Renderer.debugGL("RenderLayer vao creating");

		this.vertexCount = attribs.getUsedVertexCount();
		
		attribs.forEachAttribDataArray((int attribIndex, AttribArrayData data) -> {
			vbos.computeIfAbsent(attribIndex, index -> new VertexBuffer().gen());
			VertexBuffer vbo = vbos.get(attribIndex);
			
			ByteBuffer buffer = data.getBuffer(vertexCount);
			
			if (allowSubData)
				vbo.loadDataOrSubData(buffer);
			else
				vbo.data(buffer);

			Renderer.debugGL("RenderLayer loadData");

			vao.bindAttribPointer(vbo, attribIndex, attribs.getAttribType(attribIndex));
			Renderer.debugGL("Bind buffers to vao");
		});
	}

	public void render(int mode) {
		if(program != null)
			program.bind();
		
		if(textureBinder != null)
			textureBinder.bind(this);
		
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
	
	public void setTextureBinder(ITextureBinder textureBinder) {
		this.textureBinder = textureBinder;
	}

	public void clearVertexArrayDataBakerIfExists() {
		if (hasVertexAttribArray())
			getVertexArrayDataBaker().clear();
	}

	public void deleteVertexBuffers() {
		vbos.forEach((int index, VertexBuffer buff) -> buff.delete());
	}

	public void delete() {
		deleteVertexBuffers();
		vao.delete();
	}
	
	@FunctionalInterface
	public static interface ITextureBinder {
		public void bind(RenderLayer program);
	}
}

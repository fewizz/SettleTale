package ru.settletale.client.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.ElementArrayBuffer;
import ru.settletale.client.render.vertex.VertexArrayDataBakerIndexed;
import ru.settletale.client.render.vertex.VertexAttribType;

public class RenderLayerIndexed extends RenderLayer {
	int indexCount;
	ElementArrayBuffer ibo;

	public RenderLayerIndexed() {
		super();
		initIndexBuffer();
	}

	public RenderLayerIndexed(VertexAttribType... storages) {
		super(storages);
		initIndexBuffer();
	}

	private void initIndexBuffer() {
		ibo = new ElementArrayBuffer();
	}

	@Override
	public void compile() {
		super.compile();

		this.indexCount = ((VertexArrayDataBakerIndexed) attribs).getIndexCount();

		if (!ibo.isGenerated()) {
			ibo.gen();
		}
		
		ByteBuffer buffer = ((VertexArrayDataBakerIndexed) attribs).getIndexBuffer();
		
		if (allowSubData) {
			ibo.loadDataOrSubData(buffer);
		}
		else {
			ibo.data(buffer);
		}
	}

	@Override
	public void render(int mode) {
		program.bind();
		vao.bind();

		ibo.bind();
		GL11.glDrawElements(GL11.GL_QUADS, indexCount, GL11.GL_UNSIGNED_SHORT, MemoryUtil.NULL);
	}
	
	@Override
	public void delete() {
		super.delete();
		ibo.delete();
	}
}

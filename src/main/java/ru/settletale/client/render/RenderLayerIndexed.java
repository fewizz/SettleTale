package ru.settletale.client.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.ElementArrayBufferObject;
import ru.settletale.client.vertex.VertexAttribType;
import ru.settletale.client.vertex.VertexArrayDataBakerIndexed;

public class RenderLayerIndexed extends RenderLayer {
	int indexCount;
	ElementArrayBufferObject indexBuffer;

	public RenderLayerIndexed() {
		super();
		initIndexBuffer();
	}

	public RenderLayerIndexed(VertexAttribType... storages) {
		super(storages);
		initIndexBuffer();
	}

	private void initIndexBuffer() {
		indexBuffer = new ElementArrayBufferObject();
	}

	@Override
	public void compile() {
		super.compile();

		this.indexCount = ((VertexArrayDataBakerIndexed) attribs).getIndexCount();

		if (!indexBuffer.isGenerated()) {
			indexBuffer.gen();
		}
		
		ByteBuffer buffer = ((VertexArrayDataBakerIndexed) attribs).getIndexBuffer();
		
		if (allowSubData) {
			indexBuffer.loadDataOrSubData(buffer);
		}
		else {
			indexBuffer.data(buffer);
		}
	}

	@Override
	public void render(int mode) {
		program.bind();
		vao.bind();

		indexBuffer.bind();
		GL11.glDrawElements(GL11.GL_QUADS, indexCount, GL11.GL_UNSIGNED_SHORT, MemoryUtil.NULL);
	}
	
	@Override
	public void delete() {
		super.delete();
		
		indexBuffer.delete();
	}
}

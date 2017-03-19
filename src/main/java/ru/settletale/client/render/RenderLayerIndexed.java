package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.ElementArrayBufferObject;
import ru.settletale.client.vertex.AttributeType;
import ru.settletale.client.vertex.VertexAttributeArrayIndexed;

public class RenderLayerIndexed extends RenderLayer {
	int indexCount;
	ElementArrayBufferObject indexBuffer;

	public RenderLayerIndexed() {
		super();
		initIndexBuffer();
	}

	public RenderLayerIndexed(AttributeType... storages) {
		super(new VertexAttributeArrayIndexed(storages));
		initIndexBuffer();
	}

	private void initIndexBuffer() {
		indexBuffer = new ElementArrayBufferObject();
	}

	@Override
	public void compile(boolean allowSubDataIfPossible) {
		super.compile(allowSubDataIfPossible);

		this.indexCount = ((VertexAttributeArrayIndexed) vertexArray).getIndexCount();

		if (!indexBuffer.isGenerated()) {
			indexBuffer.gen();
		}
		indexBuffer.buffer(((VertexAttributeArrayIndexed) vertexArray).getIndexBuffer());
		if (allowSubDataIfPossible) {
			indexBuffer.loadDataOrSubData();
		}
		else {
			indexBuffer.loadData();
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

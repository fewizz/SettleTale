package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.util.DirectBufferUtils;

public class VertexArrayDataBakerIndexed extends VertexArrayDataBaker {
	private ByteBuffer indexBuffer;
	protected int indexCount = 0;

	public VertexArrayDataBakerIndexed(VertexAttribType... attribTypes) {
		this(false, attribTypes);
	}

	public VertexArrayDataBakerIndexed(boolean lazyIndexBufferInitialisation, VertexAttribType... attribTypes) {
		super(attribTypes);
		
		if (!lazyIndexBufferInitialisation) {
			initIndexBuffer();
		}
	}
	
	private void initIndexBuffer() {
		indexBuffer = MemoryUtil.memAlloc(4096);
		indexBuffer.limit(0);
	}

	public void index(int vertexIndex) {
		int id = indexCount;

		int sizeBytes = Short.BYTES;
		id *= sizeBytes;

		int limit = id + sizeBytes;

		if(indexBuffer == null) {
			initIndexBuffer();
		}
		
		if (limit > indexBuffer.capacity())
			indexBuffer = DirectBufferUtils.growBuffer(indexBuffer, 1.5F);

		indexBuffer.limit(limit);

		indexBuffer.putShort(id, (short) vertexIndex);

		indexBuffer.position(0);

		indexCount++;
	}

	public ByteBuffer getIndexBuffer() {
		return indexBuffer;
	}

	@Override
	public void clearData() {
		super.clearData();
		indexCount = 0;
	}

	public int getIndexCount() {
		return this.indexCount;
	}
}

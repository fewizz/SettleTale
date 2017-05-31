package ru.settletale.client.vertex;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.util.DirectBufferUtils;

public class VertexArrayDataBakerIndexed extends VertexArrayDataBaker {
	private ByteBuffer indexBuffer;
	protected int indexBufferSize;
	protected int indexCount = 0;

	public VertexArrayDataBakerIndexed(int size, int maxIndexCount, boolean dynamic, VertexAttribType... attribTypes) {
		this(size, maxIndexCount, dynamic, false, attribTypes);
	}

	public VertexArrayDataBakerIndexed(int size, int maxIndexCount, boolean dynamic, boolean lazyInit, VertexAttribType... attribTypes) {
		super(size, dynamic, attribTypes);

		this.indexBufferSize = maxIndexCount * Short.BYTES;
		if (!lazyInit) {
			initIndexBuffer();
		}
	}

	private void initIndexBuffer() {
		indexBuffer = MemoryUtil.memAlloc(indexBufferSize);
		indexBuffer.limit(0);
	}

	public void index(int vertexIndex) {
		int index = indexCount;

		int sizeBytes = Short.BYTES;
		index *= sizeBytes;

		int limitIndex = index + sizeBytes;

		if (indexBuffer == null) {
			initIndexBuffer();
		}

		if (limitIndex >= indexBufferSize - 1) {
			if(!dynamic) {
				throw new Error("Max index reached");
			}
			indexBuffer = DirectBufferUtils.growBuffer(indexBuffer, 1.5F);
			indexBufferSize = indexBuffer.capacity();
		}

		indexBuffer.limit(limitIndex);

		indexBuffer.putShort(index, (short) vertexIndex);

		indexBuffer.position(0);

		indexCount++;
	}

	public ByteBuffer getIndexBuffer() {
		return indexBuffer;
	}

	@Override
	public void clear() {
		super.clear();
		indexCount = 0;
	}

	public int getIndexCount() {
		return this.indexCount;
	}
}

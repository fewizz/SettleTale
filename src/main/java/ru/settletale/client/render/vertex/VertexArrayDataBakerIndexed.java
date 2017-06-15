package ru.settletale.client.render.vertex;

import java.nio.ByteBuffer;

import ru.settletale.memory.MemoryBlock;

public class VertexArrayDataBakerIndexed extends VertexArrayDataBaker {
	private MemoryBlock indexMB;//private ByteBuffer indexBuffer;
	//protected int indexBufferSize;
	protected int maxIndexCount;
	protected int indexCount = 0;

	public VertexArrayDataBakerIndexed(int expectedVertexCount, int maxIndexCount, boolean dynamic, VertexAttribType... attribTypes) {
		this(expectedVertexCount, maxIndexCount, dynamic, false, attribTypes);
	}

	public VertexArrayDataBakerIndexed(int expectedVertexCount, int expectedIndexCount, boolean dynamic, boolean lazyInit, VertexAttribType... attribTypes) {
		super(expectedVertexCount, dynamic, attribTypes);

		//this.indexBufferSize = maxIndexCount * Short.BYTES;
		this.maxIndexCount = expectedIndexCount;
		
		if (!lazyInit) {
			initIndexBuffer();
		}
	}

	private void initIndexBuffer() {
		indexMB = new MemoryBlock().allocate(maxIndexCount * Short.BYTES);
	}

	public void index(int vertexIndex) {
		if (indexMB == null) {
			initIndexBuffer();
		}

		if (indexCount * Short.BYTES >= indexMB.bytes()) {
			if(!dynamic) {
				throw new Error("Max index reached");
			}
			maxIndexCount = (int) (indexCount * growFactor);
			indexMB.reallocate(maxIndexCount * Short.BYTES);
		}

		indexMB.putShorS(indexCount++, (short) vertexIndex);
	}

	public ByteBuffer getIndexBuffer() {
		return indexMB.getAsByteBuffer(indexCount * Short.BYTES);
	}

	@Override
	public void clear() {
		super.clear();
		indexCount = 0;
		indexMB.set(0);
	}
	
	@Override
	public void delete() {
		super.delete();
		indexMB.free();
	}

	public int getIndexCount() {
		return this.indexCount;
	}
}

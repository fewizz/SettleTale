package ru.settletale.client.render;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.VertexBuffer;
import ru.settletale.memory.MemoryBlock;

public class VertexAttribArray extends MemoryBlock {
	private final VertexBuffer vertexBuffer = new VertexBuffer();
	public final Attrib attribInfo;
	int vertexNumber;
	
	public VertexAttribArray(Attrib atrtib, int vertexNumber) {
		this.attribInfo = atrtib;
		this.vertexNumber = vertexNumber;
		allocate(atrtib.componentsBytes * vertexNumber);
	}
	
	public VertexBuffer getVertexBuffer() {
		if(!vertexBuffer.isGenerated()) {
			vertexBuffer.gen();
		}
		return vertexBuffer;
	}
	
	public void freeClientData() {
		free();
	}
	
	@Deprecated
	@Override
	public void free() {
		super.free();
	}
	
	public void updateServerData() {
		updateVertexBuffer();
	}
	
	private void updateVertexBuffer() {
		if(address == MemoryUtil.NULL) {
			throw new Error("You haven't even 'begin'");
		}
		getVertexBuffer().dataOrSubData(address(), vertexNumber * attribInfo.componentsBytes);
	}
}

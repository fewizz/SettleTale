package ru.settletale.client.render;

import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.VertexBuffer;
import ru.settletale.memory.MemoryBlock;

public class VertexAttribArray extends MemoryBlock {
	private final VertexBuffer vertexBuffer = new VertexBuffer();
	public final Attrib attribInfo;
	
	public VertexAttribArray(Attrib atrtib, int vertexNumber) {
		this.attribInfo = atrtib;
		allocate(atrtib.componentsBytes * vertexNumber);
	}
	
	/*public VertexAttribArray begin(int vertexNumber) {
		allocate(attribInfo.components * vertexNumber * attribInfo.clientDataType.bytes);
		return this;
	}
	
	public VertexBuffer end() {
		updateVertexBuffer();
		free();
		return getVertexBuffer();
	}*/
	
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
		getVertexBuffer().dataOrSubData(address(), bytes());
	}
}

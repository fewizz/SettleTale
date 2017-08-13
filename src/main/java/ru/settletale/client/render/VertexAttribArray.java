package ru.settletale.client.render;

import ru.settletale.client.render.util.GLPrimitive;
import ru.settletale.memory.MemoryBlock;
import wrap.gl.VertexArray;
import wrap.gl.VertexBuffer;

public class VertexAttribArray extends MemoryBlock {
	private final VertexBuffer vertexBuffer = new VertexBuffer();
	public final int components;
	public final GLPrimitive type;
	int maxVertexNumber;
	
	public VertexAttribArray(GLPrimitive type, int components, int vertexNumber) {
		this.components = components;
		this.type = type;
		this.maxVertexNumber = vertexNumber;
		allocate(type.bytes * components * vertexNumber);
	}
	
	public VertexAttribArray(int typeCode, int components, int vertexNumber) {
		this(GLPrimitive.getFromCode(typeCode), components, vertexNumber);
	}
	
	public VertexBuffer getVertexBuffer() {
		if(!vertexBuffer.isGenerated()) {
			vertexBuffer.gen();
		}
		return vertexBuffer;
	}
	
	public void updateVertexBuffer() {
		getVertexBuffer().dataOrSubData(address(), bytes());
	}
	
	public void pointToVAO(VertexArray vao, int index, boolean normalize) {
		vao.vertexAttribPointer(getVertexBuffer(), index, components, type.code, normalize);
		vao.enableVertexAttribArray(index);
	}
	
	public void pointToVAOI(VertexArray vao, int index) {
		vao.vertexAttribIPointer(getVertexBuffer(), index, components, type.code);
		vao.enableVertexAttribArray(index);
	}
}

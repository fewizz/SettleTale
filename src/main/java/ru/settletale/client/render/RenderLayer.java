package ru.settletale.client.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.VertexArrayObject;
import ru.settletale.client.gl.VertexBufferObject;
import ru.settletale.client.vertex.PrimitiveArray;

public class RenderLayer {
	int posIndex;
	int normIndex;
	int uvIndex;
	int colorIndex;
	int flagsIndex;
	
	public boolean usedNormal;
	public boolean usedColor;
	public boolean usedUV;
	public boolean usedMaterials;
	public boolean usedTextures;
	List<Material> materials;
	VertexArrayObject vao;
	VertexBufferObject positionBuffer;
	VertexBufferObject normalBuffer;
	VertexBufferObject colorBuffer;
	VertexBufferObject uvBuffer;
	VertexBufferObject flagsBuffer;
	int vertexCount = 0;
	PrimitiveArray primitiveArray;
	ShaderProgram program;
	
	public RenderLayer(PrimitiveArray array) {
		primitiveArray = array;
		materials = new ArrayList<>();
	}
	
	public void compile() {
		vao = new VertexArrayObject().gen();
		
		vao.bind();
		positionBuffer = new VertexBufferObject().gen();
		normalBuffer = new VertexBufferObject().gen();
		colorBuffer = new VertexBufferObject().gen();
		uvBuffer = new VertexBufferObject().gen();
		flagsBuffer = new VertexBufferObject().gen();
		
		positionBuffer.buffer(primitiveArray.getBuffer(posIndex)).loadData();
		normalBuffer.buffer(primitiveArray.getBuffer(normIndex)).loadData();
		colorBuffer.buffer(primitiveArray.getBuffer(colorIndex)).loadData();
		uvBuffer.buffer(primitiveArray.getBuffer(uvIndex)).loadData();
		flagsBuffer.buffer(primitiveArray.getBuffer(flagsIndex)).loadData();
		
		vao.vertexAttribPointer(positionBuffer, 0, 4);
		vao.enableVertexAttribArray(0);
		
		if(usedNormal) {
			vao.vertexAttribPointer(normalBuffer, 1, 3);
			vao.enableVertexAttribArray(1);
		}
		
		if(usedUV) {
			vao.vertexAttribPointer(uvBuffer, 2, 2);
			vao.enableVertexAttribArray(2);
		}
		
		if(usedColor) {
			vao.vertexAttribPointer(colorBuffer, 3, 4, GL11.GL_UNSIGNED_BYTE, true);
			vao.enableVertexAttribArray(3);
		}
		
		vao.vertexAttribIntPointer(flagsBuffer, 4, 1);
		vao.enableVertexAttribArray(4);
	}
	
	public void render(int mode) {
		program.bind();
		vao.bind();
		GL11.glDrawArrays(mode, 0, vertexCount);
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		
		hash |= usedNormal ? 1 : 0;
		hash |= (usedColor ? 1 : 0) << 1;
		hash |= (usedUV ? 1 : 0) << 2;
		hash |= (usedMaterials ? 1 : 0) << 3;
		hash |= (usedTextures ? 1 : 0) << 4;
		
		return hash;
	}
}

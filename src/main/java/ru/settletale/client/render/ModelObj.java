package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.opengl.ElementArrayBufferObject;
import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.client.resource.ShaderLoader;

public class ModelObj {
	private int indexCount;
	private VertexArrayObject vao;
	private ElementArrayBufferObject ibo;
	private VertexBufferObject vbo;
	static ShaderProgram program;
	
	public void compile() {
		vao = new VertexArrayObject().gen();
		vao.vertexAttribPointer(vbo, 0, 4, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);
		
		if(program == null) {
			program = new ShaderProgram().gen();
			program.attachShader(ShaderLoader.SHADERS.get("shaders/default.vs"));
			program.attachShader(ShaderLoader.SHADERS.get("shaders/default.fs"));
			program.bind();
		}
	}
	
	public void render() {
		GL.bindDefaultVAO();
		program.bind();
		vao.bind();
		ibo.bind();
		GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_SHORT, MemoryUtil.NULL);
	}
	
	public void setVertexPositions(int indexCount, VertexBufferObject vbo, ElementArrayBufferObject ibo) {
		this.indexCount = indexCount;
		this.vbo = vbo;
		this.ibo = ibo;
	}
}

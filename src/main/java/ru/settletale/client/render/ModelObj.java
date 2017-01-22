package ru.settletale.client.render;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.client.resource.ShaderLoader;

public class ModelObj {
	private int vertexCount;
	private VertexArrayObject vao;
	private VertexBufferObject positionVBO;
	private VertexBufferObject normalVBO;
	static ShaderProgram programNormalWhite;
	
	public void compile() {
		GL.debug("ModelObj compile start");
		positionVBO.gen().loadData();
		normalVBO.gen().loadData();
		
		vao = new VertexArrayObject().gen();
		vao.vertexAttribPointer(positionVBO, 0, 4, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);
		
		vao.vertexAttribPointer(normalVBO, 1, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(1);
		
		if(programNormalWhite == null) {
			programNormalWhite = new ShaderProgram().gen();
			programNormalWhite.attachShader(ShaderLoader.SHADERS.get("shaders/default_normal_white.vs"));
			programNormalWhite.attachShader(ShaderLoader.SHADERS.get("shaders/default_normal_white.fs"));
			programNormalWhite.link();
		}
		GL.debug("ModelObj compile end");
	}
	
	public void render() {
		GL.debug("ModelObj render start");
		programNormalWhite.bind();
		GL.debug("ModelObj program bind");
		vao.bind();
		GL.debug("ModelObj vao bind");
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
		GL.debug("ModelObj render end");
	}
	
	public void setVertexPositions(int vertexCount, ByteBuffer positions, ByteBuffer normals) {
		positionVBO = new VertexBufferObject();
		normalVBO = new VertexBufferObject();
		
		positionVBO.buffer(positions);
		normalVBO.buffer(normals);
		
		this.vertexCount = vertexCount;
	}
}

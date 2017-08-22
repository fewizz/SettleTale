package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;

import ru.settletale.client.gl.Query;
import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.GLBuffer.BufferUsage;
import ru.settletale.client.gl.Shader.ShaderType;
import ru.settletale.client.render.vertex.VertexArrayDataBaker;
import ru.settletale.client.render.vertex.VertexArrayRenderer;
import ru.settletale.client.render.vertex.VertexArrayRenderer.GLDrawFunc;
import ru.settletale.client.resource.loader.ShaderSourceLoader;

public class ObjModelRenderer {
	TexturedMaterialBinder tb;
	VertexArrayRenderer layer;
	
	static final Query q = new Query(GL33.GL_TIME_ELAPSED);
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	public void compile(VertexArrayDataBaker baker) {
		Renderer.debugGL("ModelObj compile start");
		
		if(!PROGRAM.isGenerated()) {
			PROGRAM.gen();
			PROGRAM.attachShader(new Shader().gen(ShaderType.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/obj.vs")));
			PROGRAM.attachShader(new Shader().gen(ShaderType.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/obj.fs")));
			PROGRAM.link();
		}
		
		layer = new VertexArrayRenderer();
		layer.setShaderProgram(PROGRAM);
		layer.compile(baker, BufferUsage.STATIC_DRAW);
		baker.delete();
		
		Renderer.debugGL("ModelObj compile end");
	}
	
	public void render() {
		if(!q.isGenerated()) {
			q.gen();
		}
		Renderer.debugGL("ModelObj render start");
		
		tb.bindTextures();
		tb.setDiffuseTexturesUniformArraylocation(0);
		tb.setBumpTexturesUniformArraylocation(32);
		tb.updateUniforms(PROGRAM);
		
		q.begin();
		layer.render(GLDrawFunc.DRAW_ARRAYS, GL11.GL_TRIANGLES);
		q.end();
		System.out.println(q.getResult(GL15.GL_QUERY_RESULT) / 1000000F);
		
		Renderer.debugGL("ModelObj render end");
	}
	
	public void setTextureAndMaterialBinder(TexturedMaterialBinder tb) {
		this.tb = tb;
	}
}

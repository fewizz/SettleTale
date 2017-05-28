package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Query;
import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Shader.Type;
import ru.settletale.client.resource.loader.ShaderSourceLoader;
import ru.settletale.client.vertex.VertexArrayDataBaker;

public class ObjModel {
	TexturedMaterialBinder tb;
	RenderLayer layer;
	
	static final Query q = new Query(GL33.GL_TIME_ELAPSED);
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	public void compile(VertexArrayDataBaker baker) {
		GL.debug("ModelObj compile start");
		
		if(!PROGRAM.isGenerated()) {
			PROGRAM.gen();
			PROGRAM.attachShader(new Shader().gen(Type.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/obj.vs")));
			PROGRAM.attachShader(new Shader().gen(Type.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/obj.fs")));
			PROGRAM.link();
		}
		
		layer = new RenderLayer();
		layer.setVertexArrayDataBaker(baker);
		layer.setShaderProgram(PROGRAM);
		layer.compile();
		baker.delete();
		
		GL.debug("ModelObj compile end");
	}
	
	public void render() {
		if(!q.isGenerated()) {
			q.gen();
		}
		GL.debug("ModelObj render start");
		
		tb.bindTextures();
		tb.setDiffuseTexturesUniformArraylocation(0);
		tb.setBumpTexturesUniformArraylocation(32);
		tb.updateUniforms(PROGRAM);
		
		q.begin();
		layer.render(GL11.GL_TRIANGLES);
		q.end();
		System.out.println(q.getResult(GL15.GL_QUERY_RESULT) / 1000000F);
		
		GL.debug("ModelObj render end");
	}
	
	public void setTextureAndMaterialBinder(TexturedMaterialBinder tb) {
		this.tb = tb;
	}
}

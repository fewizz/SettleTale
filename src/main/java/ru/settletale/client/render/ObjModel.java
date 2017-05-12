package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.vertex.VertexArrayDataBaker;

public class ObjModel {
	TexturedMaterialBinder tb;
	RenderLayer layer;
	
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	public void compile(VertexArrayDataBaker baker) {
		GL.debug("ModelObj compile start");
		
		if(!PROGRAM.isGenerated()) {
			PROGRAM.gen();
			PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/obj.vs"));
			PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/obj.fs"));
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
		GL.debug("ModelObj render start");
		
		tb.bindTextures();
		tb.setDiffuseTexturesUniformArraylocation(0);
		tb.updateUniforms(PROGRAM);
		layer.render(GL11.GL_TRIANGLES);
		
		GL.debug("ModelObj render end");
	}
	
	public void setTextureAndMaterialBinder(TexturedMaterialBinder tb) {
		this.tb = tb;
	}
}

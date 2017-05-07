package ru.settletale.client.render;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;

public class ObjModel {
	TextureAndMaterialBinder tb;
	
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	public void compile() {
		GL.debug("ModelObj compile start");
		
		GL.debug("ModelObj compile end");
	}
	
	public void render() {
		GL.debug("ModelObj render start");
		
		
		
		GL.debug("ModelObj render end");
	}
	
	public void setTextureAndMaterialBinder(TextureAndMaterialBinder tb) {
		this.tb = tb;
	}
}

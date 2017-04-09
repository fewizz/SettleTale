package ru.settletale.client.render;

import java.util.List;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.UniformBuffer;

public class ObjModel {
	private String mtlPath;
	private MTLLib mtl;
	private List<String> materialNames;
	private UniformBuffer ubo;
	
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	public void compile() {
		GL.debug("ModelObj compile start");
		GL.debug("ModelObj compile end");
	}
	
	public void render() {
		GL.debug("ModelObj render start");
		
		
		
		GL.debug("ModelObj render end");
	}
	
	public void setMtlLibPath(String name) {
		this.mtlPath = name;
	}
	
	public void setMaterialNames(List<String> materials) {
		this.materialNames = materials;
	}
}

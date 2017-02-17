package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderProgram extends NameableAbstract<ShaderProgram> {
	
	@Override
	public int genInternal() {
		return GL20.glCreateProgram();
	}
	
	@Override
	public boolean isBase() {
		return true;
	}
	
	public void attachShader(Shader shader) {
		GL20.glAttachShader(id, shader.id);
	}
	
	public void attachShaders(Shader... shaders) {
		for(Shader sh : shaders) 
			GL20.glAttachShader(id, sh.id);
	}
	
	public void link() {
		GL20.glLinkProgram(id);
		
		if(GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println("Program not compiled!");
			System.err.println(GL20.glGetProgramInfoLog(id));
		}
	}

	@Override
	public void bindInternal() {
		GL20.glUseProgram(id);
	}

	@Override
	public void unbindInternal() {
		GL20.glUseProgram(0);
	}

	@Override
	public void deleteInternal() {
		GL20.glDeleteProgram(id);
	}
}

package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderProgram extends NameableAdapter<ShaderProgram> {
	public static int lastID = -1;
	
	@Override
	public int internalGet() {
		return GL20.glCreateProgram();
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
	public void setLastBoundID(int id) {
		lastID = id;
	}

	@Override
	public int getLastBoundID() {
		return lastID;
	}

	@Override
	public void bindInternal() {
		GL20.glUseProgram(id);
	}

	@Override
	public void unbindInternal() {
		GL20.glUseProgram(0);
	}
}

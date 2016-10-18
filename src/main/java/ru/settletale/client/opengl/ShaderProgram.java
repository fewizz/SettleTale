package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL20;

public class ShaderProgram extends NameableAdapter {
	public static int lastID = -1;
	
	public ShaderProgram(int id) {
		super(id);
	}
	
	public void attachShader(Shader shader) {
		GL20.glAttachShader(id, shader.id);
	}
	
	public void link() {
		GL20.glLinkProgram(id);
	}
	
	public static ShaderProgram gen() {
		return new ShaderProgram(GL20.glCreateProgram());
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

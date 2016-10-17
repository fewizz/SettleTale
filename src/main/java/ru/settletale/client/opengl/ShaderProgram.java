package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL20;

public class ShaderProgram {
	public final int id;
	
	public ShaderProgram(int id) {
		this.id = id;
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
}

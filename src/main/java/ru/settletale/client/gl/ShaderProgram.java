package ru.settletale.client.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class ShaderProgram extends GLBindableObject<ShaderProgram> {

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
		for (Shader sh : shaders)
			GL20.glAttachShader(id, sh.id);
	}

	public void link() {
		GL20.glLinkProgram(id);

		if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println("Program is not compiled!");
			System.err.println(GL20.glGetProgramInfoLog(id));
		}
	}

	@Override
	public void bindInternal() {
		GL20.glUseProgram(id);
	}

	@Override
	public void deleteInternal() {
		GL20.glDeleteProgram(id);
	}

	public int getAttributeLocation(String name) {
		return GL20.glGetAttribLocation(id, name);
	}

	public int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(id, name);
	}

	public void setUniformInt(int location, int value) {
		bind();
		GL20.glUniform1i(location, value);
	}

	public void setUniformMatrix4f(int location, Matrix4f mat) {
		bind();
		try (MemoryStack ms = MemoryStack.stackPush()) {
			FloatBuffer buff = ms.mallocFloat(4 * 4);
			GL20.glUniformMatrix4fv(location, false, mat.get(buff));
		}
	}

	public void setUniformIntArray(int location, IntBuffer value) {
		bind();
		GL20.glUniform1iv(location, value);
	}
}

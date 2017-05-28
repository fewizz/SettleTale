package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Shader extends GLBindableObject<Shader> {
	Type type;
	String source;

	public Shader gen(Type type) {
		this.type = type;
		super.gen();
		return this;
	}
	
	@Deprecated
	@Override
	public Shader gen() {
		return super.gen();
	}
	
	@Override
	public void deleteInternal() {
		GL20.glDeleteShader(id);
	}

	@Override
	public boolean isBase() {
		return true;
	}

	public Shader source(String source) {
		this.source = source;
		GL20.glShaderSource(id, source);
		return this;
	}

	@Override
	public int genInternal() {
		return GL20.glCreateShader(type.glCode);
	}

	@Override
	public void bindInternal() {
	}

	@Override
	public void unbindInternal() {
	}

	public Shader compile() {
		GL20.glCompileShader(id);
		if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println(source + " \n" + type.name + " not compiled!");
			System.err.println(GL20.glGetShaderInfoLog(id));
		}
		return this;
	}

	public enum Type {
		VERTEX("VERTEX_SHADER", GL20.GL_VERTEX_SHADER),
		FRAGMENT("FRAGMENT_SHADER", GL20.GL_FRAGMENT_SHADER),
		GEOMETRY("GEOMETRY_SHADER", GL32.GL_GEOMETRY_SHADER);

		String name;
		int glCode;

		Type(String name, int intGL) {
			this.name = name;
			this.glCode = intGL;
		}
	}
}

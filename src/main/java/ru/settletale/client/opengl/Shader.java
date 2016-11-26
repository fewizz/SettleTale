package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import ru.settletale.client.resource.ShaderLoader;

public class Shader {
	int id;
	final Type type;
	final String file;
	
	public Shader(Type type, String file) {
		this.type = type;
		this.file = file;
	}
	
	public Shader compile() {
		GL20.glCompileShader(id);
		if(GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println(type.name + " " + file + " not compiled!");
			System.err.println(GL20.glGetShaderInfoLog(id));
		}
		return this;
	}
	
	public Shader gen() {
		id = GL20.glCreateShader(type.glCode);
		GL20.glShaderSource(id, ShaderLoader.shaderSources.get(file));
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

package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Shader {
	int id;
	final Type type;
	final String source;
	
	public Shader(Type type, String source) {
		this.type = type;
		this.source = source;
	}
	
	public Shader compile() {
		GL20.glCompileShader(id);
		if(GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println(type.name + " " + source + " \nnot compiled!");
			System.err.println(GL20.glGetShaderInfoLog(id));
		}
		return this;
	}
	
	public Shader gen() {
		id = GL20.glCreateShader(type.glCode);
		GL20.glShaderSource(id, source);
		return this;
	}
	
	public enum Type {
		VERTEX("VERTEX_SHADER", "vs", GL20.GL_VERTEX_SHADER),
		FRAGMENT("FRAGMENT_SHADER", "fs", GL20.GL_FRAGMENT_SHADER),
		GEOMETRY("GEOMETRY_SHADER", "gs", GL32.GL_GEOMETRY_SHADER);
		
		String name;
		String extension;
		int glCode;
		
		Type(String name, String extension, int intGL) {
			this.name = name;
			this.extension = extension;
			this.glCode = intGL;
		}
		
		public static Type getByExtension(String extension) {
			for(Type type : Type.values()) {
				if(type.extension.equals(extension)) {
					return type;
				}
			}
			
			return null;
		}
	}
}

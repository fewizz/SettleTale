package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Shader extends NameableAbstract<Shader> {
	final Type type;
	final String source;
	
	public Shader(Type type, String source) {
		this.type = type;
		this.source = source;
	}
	
	@Override
	public Shader gen() {
		super.gen();
		
		GL20.glShaderSource(id, source);
		
		return this;
	}
	
	@Override
	public void deleteInternal() {
		GL20.glDeleteShader(id);
	}
	
	@Override
	public boolean isBase() {
		return true;
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
		if(GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println(type.name + "\n" + source + " \nnot compiled!");
			System.err.println(GL20.glGetShaderInfoLog(id));
		}
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

package wrap.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class Shader extends GLObject<Shader> {
	ShaderType type;
	String source;

	public Shader gen(ShaderType type) {
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
		GL20.glDeleteShader(getID());
	}

	@Override
	public boolean isBase() {
		return true;
	}

	public Shader source(String source) {
		this.source = source;
		GL20.glShaderSource(getID(), source);
		return this;
	}

	@Override
	public int genInternal() {
		return GL20.glCreateShader(type.glCode);
	}

	public Shader compile() {
		GL20.glCompileShader(getID());
		if (GL20.glGetShaderi(getID(), GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.err.println(source + " \n" + type.name + " not compiled!");
			System.err.println(GL20.glGetShaderInfoLog(getID()));
		}
		return this;
	}

	public enum ShaderType {
		VERTEX("VERTEX_SHADER", GL20.GL_VERTEX_SHADER),
		FRAGMENT("FRAGMENT_SHADER", GL20.GL_FRAGMENT_SHADER),
		GEOMETRY("GEOMETRY_SHADER", GL32.GL_GEOMETRY_SHADER);

		String name;
		int glCode;

		ShaderType(String name, int intGL) {
			this.name = name;
			this.glCode = intGL;
		}
	}
}

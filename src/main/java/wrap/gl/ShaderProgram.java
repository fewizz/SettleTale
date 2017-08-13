package wrap.gl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import ru.settletale.memory.MemoryBlock;

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
		GL20.glAttachShader(getID(), shader.getID());
	}

	public void attachShaders(Shader... shaders) {
		for (Shader sh : shaders)
			GL20.glAttachShader(getID(), sh.getID());
	}

	public void link() {
		GL20.glLinkProgram(getID());

		if (GL20.glGetProgrami(getID(), GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			System.err.println("Program is not compiled! Error message:");
			System.err.println(GL20.glGetProgramInfoLog(getID()));
		}
	}
	
	public void use() {
		this.bind();
	}
	
	@Deprecated
	@Override
	public void bind() {
		super.bind();
	}

	@Override
	public void bindInternal() {
		GL20.glUseProgram(getID());
	}

	@Override
	public void deleteInternal() {
		GL20.glDeleteProgram(getID());
	}

	public int getAttributeLocation(String name) {
		return GL20.glGetAttribLocation(getID(), name);
	}

	public int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(getID(), name);
	}

	public void setUniformInt(int location, int value) {
		use();
		GL20.glUniform1i(location, value);
	}

	public void setUniformMatrix4f(int location, Matrix4f mat) {
		use();
		try (MemoryStack ms = MemoryStack.stackPush()) {
			FloatBuffer buff = ms.mallocFloat(4 * 4);
			GL20.glUniformMatrix4fv(location, false, mat.get(buff));
		}
	}

	public void setUniformIntArray(int location, IntBuffer value) {
		use();
		GL20.glUniform1iv(location, value);
	}
	
	public void setUniformIntArray(int location, MemoryBlock value) {
		use();
		GL20.nglUniform1iv(location, value.ints(), value.address());
	}
}

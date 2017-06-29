package wrap.gl;

import org.lwjgl.opengl.GL31;

public class UniformBuffer extends GLBuffer<UniformBuffer> {

	public UniformBuffer() {
		super(GL31.GL_UNIFORM_BUFFER);
	}
}

package wrap.gl;

import org.lwjgl.opengl.GL15;

public class VertexBuffer extends GLBuffer<VertexBuffer> {
	public int attribLocation = -1;
	
	public VertexBuffer() {
		super(GL15.GL_ARRAY_BUFFER);
	}
}
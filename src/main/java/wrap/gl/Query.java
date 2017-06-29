package wrap.gl;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;

public class Query extends GLObject<Query> {
	int type = -1;

	public Query(int type) {
		this.type = type;
	}
	
	@Override
	public boolean isBase() {
		return true;
	}

	@Override
	public int genInternal() {
		return GL15.glGenQueries();
	}

	public void begin() {
		GL15.glBeginQuery(type, getID());
	}
	
	public void end() {
		GL15.glEndQuery(type);
	}
	
	public void getResult(int type, IntBuffer buffer) {
		GL15.glGetQueryObjectiv(getID(), type, buffer);
	}
	
	public int getResult(int type) {
		return GL15.glGetQueryObjecti(getID(), type);
	}

	@Override
	public void deleteInternal() {
		GL15.glDeleteQueries(getID());
	}

}

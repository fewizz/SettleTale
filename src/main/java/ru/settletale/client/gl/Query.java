package ru.settletale.client.gl;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL15;

public class Query extends GLObject<Query> {
	int target = -1;
	
	public Query gen(int target) {
		this.target = target;
		return super.gen();
	}

	@Override
	public int genInternal() {
		return GL15.glGenQueries();
	}

	public void begin() {
		GL15.glBeginQuery(target, getID());
	}
	
	public void end() {
		GL15.glEndQuery(target);
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

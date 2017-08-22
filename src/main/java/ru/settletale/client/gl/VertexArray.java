package ru.settletale.client.gl;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.render.VertexAttribArray;

public class VertexArray extends GLBindableObject<VertexArray> {
	public static final VertexArray DEFAULT = new VertexArray() {
		@Override
		public VertexArray gen() {
			throw new Error();
		}
		
		@Override
		public int getID() {
			return 0;
		}
		
		@Override
		public void delete() {
			throw new Error();
		}
	};
	
	public VertexArray() {
		super(VertexArray.class);
	}
	
	@Override
	public VertexArray gen() {
		return super.gen();
	}
	
	@Override
	public int genInternal() {
		return GL30.glGenVertexArrays();
	}
	
	void vertexAttribPointer(GLBuffer<?> buffer, int index, int size, int type, boolean normalized, int stride) {
		bind();
		buffer.bind();
		GL20.glVertexAttribPointer(index, size, type, normalized, stride, MemoryUtil.NULL);
	}
	
	void vertexAttribIPointer(GLBuffer<?> buffer, int index, int size, int type, int stride) {
		bind();
		buffer.bind();
		GL30.glVertexAttribIPointer(index, size, type, stride, MemoryUtil.NULL);
	}
	
	void enableVertexAttribArray(int index) {
		if(GL.version >= 45) {
			GL45.glEnableVertexArrayAttrib(getID(), index);
			return;
		}
		bind();
		GL20.glEnableVertexAttribArray(index);
	}
	
	void disableVertexAttribArray(int index) {
		if(GL.version >= 45) {
			GL45.glDisableVertexArrayAttrib(getID(), index);
			return;
		}
		bind();
		GL20.glDisableVertexAttribArray(index);
	}
	
	public void vertexAttribPointer(VertexAttribArray attribArray) {
		if(attribArray.attribInfo.serverDataType.isIntegral)
			vertexAttribIPointer(attribArray.getVertexBuffer(), attribArray.attribInfo.location, attribArray.attribInfo.components, attribArray.attribInfo.clientDataType.code, 0);
		else
			vertexAttribPointer(attribArray.getVertexBuffer(), attribArray.attribInfo.location, attribArray.attribInfo.components, attribArray.attribInfo.clientDataType.code, attribArray.attribInfo.normalize, 0);
		
		enableVertexAttribArray(attribArray.attribInfo.location);
	}
	
	@Override
	public void deleteInternal() {
		GL30.glDeleteVertexArrays(getID());
	}

	@Override
	public void bindInternal() {
		GL30.glBindVertexArray(getID());
	}
}

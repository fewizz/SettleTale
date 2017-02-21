package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.ElementArrayBufferObject;
import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.VertexArrayObject;
import ru.settletale.client.gl.VertexBufferObject;
import ru.settletale.client.vertex.VertexArray;
import ru.settletale.client.vertex.VertexArray.StorageInfo;
import ru.settletale.client.vertex.VertexArrayIndexed;
import ru.settletale.util.ClientUtils;

public class RenderLayer {
	int vertexCount;
	int indexCount;
	VertexArray vertexArray;
	VertexArrayObject vao;
	ShaderProgram program;
	VertexBufferObject[] buffers;
	ElementArrayBufferObject indexBuffer = null;
	boolean indexed;
	int bufferCount;

	public RenderLayer() {
		this(false);
	}
	
	public RenderLayer(StorageInfo... storages) {
		this(false, storages);
	}

	public RenderLayer(boolean indexed, StorageInfo... storages) {
		this(indexed);
		
		if(indexed) {
			this.setVertexArray(new VertexArrayIndexed(false, storages));
		}
		else {
			this.setVertexArray(new VertexArray(storages));
		}
	}
	
	public RenderLayer(boolean indexed) {
		buffers = new VertexBufferObject[16];
		vao = new VertexArrayObject();
		this.indexed = indexed;

		if (this.indexed) {
			indexBuffer = new ElementArrayBufferObject();
		}
	}
	
	public void setVertexArray(VertexArray va) {
		this.vertexArray = va;
	}
	
	public VertexArray getVertexArray() {
		return this.vertexArray;
	}

	public void compile() {
		compile(false);
	}

	public void compile(boolean allowSubDataIfPossible) {
		if (!vao.isGenerated()) {
			vao.gen();
		}
		vao.bind();

		GL.debug("RenderLayer vao creating");

		this.bufferCount = vertexArray.getStorageCount();
		this.vertexCount = vertexArray.getVertexCount();
		
		if(indexed) {
			this.indexCount = ((VertexArrayIndexed)vertexArray).getIndexCount();
		}
		
		for (int i = 0; i < bufferCount; i++) {
			VertexBufferObject vbo = buffers[i];

			if(vbo == null) {
				vbo = new VertexBufferObject(); 
				vbo.gen();
				buffers[i] = vbo;
			}

			vbo.buffer(vertexArray.getBuffer(i));

			if (allowSubDataIfPossible) {
				vbo.loadDataOrSubData();
			}
			else {
				vbo.loadData();
			}

			GL.debug("RenderLayer loadData");

			StorageInfo si = vertexArray.getStorageInfo(i);

			vao.vertexAttribPointer(vbo, i, si.getElementCount(), ClientUtils.getGLPrimitive(si.getPrimitiveType()), si.isNormalised());
			vao.enableVertexAttribArray(i);
		}

		if (this.indexed) {
			if (!indexBuffer.isGenerated()) {
				indexBuffer.gen();
			}
			indexBuffer.buffer(((VertexArrayIndexed)vertexArray).getIndexBuffer());
			if (allowSubDataIfPossible) {
				indexBuffer.loadDataOrSubData();
			}
			else {
				indexBuffer.loadData();
			}
		}
	}

	public void render(int mode) {
		program.bind();
		vao.bind();

		if (this.indexed) {
			indexBuffer.bind();
			GL11.glDrawElements(GL11.GL_QUADS, indexCount, GL11.GL_UNSIGNED_SHORT, MemoryUtil.NULL);
		}
		else {
			GL11.glDrawArrays(mode, 0, vertexCount);
		}
	}

	public void setShaderProgram(ShaderProgram program) {
		this.program = program;
	}
	
	public void delete() {
		for(int i = 0; i < bufferCount; i++) {
			buffers[i].delete();
		}
		
		bufferCount = 0;
		
		if(indexed) {
			indexBuffer.delete();
		}
		
		vao.delete();
	}
}

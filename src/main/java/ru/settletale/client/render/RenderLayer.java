package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.VertexArrayObject;
import ru.settletale.client.gl.VertexBufferObject;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.util.ClientUtils;

public class RenderLayer extends PrimitiveArray {
	VertexArrayObject vao;
	ShaderProgram program;
	VertexBufferObject[] buffers;
	boolean[] normalisations;
	int bufferCount;
	
	public RenderLayer(StorageInfo... storages) {
		super(storages);
		normalisations = new boolean[16];
		vao = new VertexArrayObject();
	}
	
	public void compile() {
		compile(false);
	}
	
	public void compile(boolean allowSubDataIfPossible) {
		if(!vao.isGenerated()) {
			vao.gen();
		}
		vao.bind();
		
		GL.debug("RenderLayer vao creating");
		
		for(int i = 0; i < bufferCount; i++) {
			VertexBufferObject vbo = buffers[i];
			
			if(!vbo.isGenerated()) {
				vbo.gen();
			}
			
			vbo.buffer(getBuffer(i));
			
			if(allowSubDataIfPossible) {
				vbo.loadDataOrSubData();
			}
			else {
				vbo.loadData();
			}
			
			GL.debug("RenderLayer loadData");
			
			StorageInfo si = getStorageInfo(i);
			
			vao.vertexAttribPointer(vbo, i, si.getElementCount(), ClientUtils.getGLPrimitive(si.getPrimitiveType()), normalisations[i]);
			vao.enableVertexAttribArray(i);
		}
	}
	
	public void render(int mode) {
		program.bind();
		vao.bind();
		GL11.glDrawArrays(mode, 0, vertexCount);
	}
	
	public void setShaderProgram(ShaderProgram program) {
		this.program = program;
	}
	
	public void enableNormalisation(int index) {
		this.normalisations[index] = true;
	}
	
	@Override
	protected void addStorage(StorageInfo es) {
		super.addStorage(es);
		
		if(buffers == null) {
			buffers = new VertexBufferObject[16];
		}
		
		buffers[bufferCount++] = new VertexBufferObject();
	}
}

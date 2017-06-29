package ru.settletale.client.render.vertex;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import wrap.gl.ElementArrayBuffer;
import wrap.gl.GLBuffer.BufferUsage;

public class VertexArrayRendererIndexed extends VertexArrayRenderer {
	int indexCount;
	ElementArrayBuffer ibo;

	public VertexArrayRendererIndexed() {
		super();
		initIndexBuffer();
	}

	private void initIndexBuffer() {
		ibo = new ElementArrayBuffer();
	}

	@Override
	public void compile(VertexArrayDataBaker baker, BufferUsage usage) {
		super.compile(baker, usage);
		VertexArrayDataBakerIndexed ibaker = (VertexArrayDataBakerIndexed) baker;

		this.indexCount = ibaker.getIndexCount();

		if (!ibo.isGenerated()) {
			ibo.gen();
		}
		
		ibo.dataOrSubData(ibaker.getIndexMemoryBlock().address(), ibaker.getIndexBufferSizeInBytes());
	}

	@Override
	@Deprecated
	public void render(GLDrawFunc drawFunc, int mode) {
		super.render(drawFunc, mode);
	}
	
	public void renderIndexed(GLDrawIndexedFunc drawFunc, int mode) {
		if(program != null)
			program.bind();
		
		vao.bind();
		
		if(preRenderListeners != null)
			preRenderListeners.forEach(listener -> listener.run());
		
		ibo.bind();
		drawFunc.execute(this, mode, vertexCount);
	}
	
	@Override
	public void delete() {
		super.delete();
		ibo.delete();
	}
	
	public static enum GLDrawIndexedFunc {
		DRAW_ELEMENTS {
			@Override
			void execute(VertexArrayRendererIndexed var, int mode, int vertexCount) {
				GL11.glDrawElements(GL11.GL_QUADS, var.indexCount, GL11.GL_UNSIGNED_SHORT, MemoryUtil.NULL);
			}
		};
		
		abstract void execute(VertexArrayRendererIndexed var, int mode, int vertexCount);
	}
}

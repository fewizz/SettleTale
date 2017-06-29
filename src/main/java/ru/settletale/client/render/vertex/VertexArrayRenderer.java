package ru.settletale.client.render.vertex;

import java.util.ArrayDeque;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

import ru.settletale.client.render.Renderer;
import wrap.gl.ShaderProgram;
import wrap.gl.VertexArray;
import wrap.gl.VertexBuffer;
import wrap.gl.GLBuffer.BufferUsage;

public class VertexArrayRenderer {
	protected final VertexArray vao;
	public ShaderProgram program;
	protected final IntObjMap<VertexBuffer> vbos;
	protected int vertexCount;
	protected Queue<Runnable> preRenderListeners;
	protected Queue<Runnable> preCompileListeners;

	public VertexArrayRenderer() {
		vbos = HashIntObjMaps.newMutableMap(0);
		vao = new VertexArray();
	}

	public final void compile(VertexArrayDataBaker baker) {
		this.compile(baker, BufferUsage.STATIC_DRAW);
	}
	
	public void compile(VertexArrayDataBaker baker, BufferUsage usage) {
		if (!vao.isGenerated())
			vao.gen();

		vao.bind();

		Renderer.debugGL("RenderLayer vao creating");

		this.vertexCount = baker.getUsedVertexCount();
		
		baker.forEachAttribDataArray((int attribIndex, AttribArrayData data) -> {
			vbos.computeIfAbsent(attribIndex, index -> new VertexBuffer().gen());
			VertexBuffer vbo = vbos.get(attribIndex);
			
			vbo.usage(usage);
			
			vbo.dataOrSubData(data.getMemoryBlock().address(), data.getSizeInBytes());

			Renderer.debugGL("RenderLayer loadData");

			vao.bindAttribPointer(vbo, attribIndex, baker.getAttribType(attribIndex));
			
			Renderer.debugGL("Bind buffers to vao");
		});
	}

	public void render(GLDrawFunc drawFunc, int mode) {
		if(program != null)
			program.bind();
		
		vao.bind();
		
		if(preRenderListeners != null)
			preRenderListeners.forEach(listener -> listener.run());
		
		drawFunc.execute(this, mode, vertexCount);
	}

	public void setShaderProgram(ShaderProgram program) {
		this.program = program;
	}
	
	public VertexArrayRenderer onPreRender(Runnable action) {
		if(preRenderListeners == null) 
			preRenderListeners = new ArrayDeque<>();
		preRenderListeners.add(action);
		return this;
	}
	
	public VertexArrayRenderer onPreCompile(Runnable action) {
		if(preCompileListeners == null) 
			preCompileListeners = new ArrayDeque<>();
		preCompileListeners.add(action);
		return this;
	}

	public void deleteVertexBuffers() {
		vbos.forEach((int index, VertexBuffer buff) -> buff.delete());
	}

	public void delete() {
		deleteVertexBuffers();
		vao.delete();
	}
	
	public static enum GLDrawFunc {
		DRAW_ARRAYS {
			@Override
			void execute(VertexArrayRenderer var, int mode, int vertexCount) {
				GL11.glDrawArrays(mode, 0, vertexCount);
			}
		};
		
		abstract void execute(VertexArrayRenderer var, int mode, int vertexCount);
	}
}

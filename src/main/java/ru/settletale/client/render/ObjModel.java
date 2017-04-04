package ru.settletale.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.UniformBufferObject;
import ru.settletale.client.gl.VertexArrayObject;
import ru.settletale.client.gl.VertexBufferObject;
import ru.settletale.client.resource.MtlLibLoader;
import ru.settletale.client.resource.ShaderLoader;

public class ObjModel {
	private String mtlPath;
	private MTLLib mtl;
	private List<String> materialNames;
	private int vertexCount;
	private VertexArrayObject vao;
	private VertexBufferObject positionVBO;
	private VertexBufferObject normalVBO;
	private VertexBufferObject uvVBO;
	private VertexBufferObject flagsVBO;
	private UniformBufferObject ubo;
	private IntBuffer textureIDs;
	
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	public void compile() {
		GL.debug("ModelObj compile start");
		
		mtl = MtlLibLoader.MTLS.get(mtlPath);
		
		if(mtl == null) {
			System.out.println("Model is not using texture!");
			mtl = MTLLib.DEFAULT;
		}
		
		//positionVBO.gen().loadData();
		//normalVBO.gen().loadData();
		//flagsVBO.gen().loadData();
		//uvVBO.gen().loadData();
		//ubo = new UniformBufferObject().gen();
		
		vao = new VertexArrayObject().gen();
		vao.vertexAttribPointer(positionVBO, 0, 4, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);
		
		vao.vertexAttribPointer(normalVBO, 1, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(1);
		
		vao.vertexAttribPointer(uvVBO, 2, 2, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(2);
		
		vao.vertexAttribIntPointer(flagsVBO, 3, 1, GL11.GL_INT, 0);
		vao.enableVertexAttribArray(3);
		
		ByteBuffer buff = MemoryUtil.memAlloc(materialNames.size() * Float.BYTES * 4);
		textureIDs = MemoryUtil.memAllocInt(materialNames.size());
		
		for(int i = 0; i < materialNames.size(); i++) {
			Material m = mtl.getMaterial(materialNames.get(i));
			
			buff.putFloat(m.colorDiffuse.x);
			buff.putFloat(m.colorDiffuse.y);
			buff.putFloat(m.colorDiffuse.z);
			buff.putFloat(1F);
			
			textureIDs.put(i);
		}
		buff.flip();
		textureIDs.flip();
		
		//ubo.buffer(uBuff);
		//ubo.loadData();
		
		if(!PROGRAM.isGenerated()) {
			PROGRAM.gen();
			PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/obj.vs"));
			PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/obj.fs"));
			PROGRAM.link();
		}
		GL.debug("ModelObj compile end");
	}
	
	public void render() {
		GL.debug("ModelObj render start");
		for(int i = 0; i < materialNames.size(); i++) {
			GL.bindTextureUnit(i, mtl.getMaterial(materialNames.get(i)).textureDiffuse);
		}
		
		PROGRAM.bind();
		GL.debug("ModelObj program bind");
		vao.bind();
		GL.debug("ModelObj vao bind");
		
		GL20.glUniform1iv(0, textureIDs);
		GL.bindBufferBase(ubo, GlobalUniforms.MATERIALS);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
		GL.debug("ModelObj render end");
	}
	
	public void setVertexInfo(int vertexCount, ByteBuffer positions, ByteBuffer normals, ByteBuffer uvs, ByteBuffer flags) {
		positionVBO = new VertexBufferObject();
		normalVBO = new VertexBufferObject();
		uvVBO = new VertexBufferObject();
		flagsVBO = new VertexBufferObject();
		
		//positionVBO.buffer(positions);
		//normalVBO.buffer(normals);
		//uvVBO.buffer(uvs);
		//flagsVBO.buffer(flags);
		
		this.vertexCount = vertexCount;
	}
	
	public void setMtlLibPath(String name) {
		this.mtlPath = name;
	}
	
	public void setMaterials(List<String> materials) {
		this.materialNames = materials;
	}
}

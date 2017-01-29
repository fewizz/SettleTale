package ru.settletale.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.UniformBufferObject;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.client.resource.MtlLibLoader;
import ru.settletale.client.resource.ShaderLoader;

public class ObjModel {
	private String mtlName;
	private MTLLib mtl;
	private List<String> materialNames;
	private int vertexCount;
	private VertexArrayObject vao;
	private VertexBufferObject positionVBO;
	private VertexBufferObject normalVBO;
	private VertexBufferObject uvVBO;
	private VertexBufferObject matidsVBO;
	private UniformBufferObject ubo;
	private IntBuffer textureIDs;
	
	static ShaderProgram programObj;
	
	public void compile() {
		GL.debug("ModelObj compile start");
		
		mtl = MtlLibLoader.MTLS.get(mtlName);
		
		if(mtl == null) {
			System.out.println("Model is not using texture!");
			mtl = MTLLib.DEFAULT;
		}
		
		positionVBO.gen().loadData();
		normalVBO.gen().loadData();
		matidsVBO.gen().loadData();
		uvVBO.gen().loadData();
		ubo = new UniformBufferObject().gen();
		
		vao = new VertexArrayObject().gen();
		vao.vertexAttribPointer(positionVBO, 0, 4, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);
		
		vao.vertexAttribPointer(normalVBO, 1, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(1);
		
		vao.vertexAttribPointer(uvVBO, 2, 2, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(2);
		
		vao.vertexAttribIPointer(matidsVBO, 3, 1, GL11.GL_INT, 0);
		vao.enableVertexAttribArray(3);
		
		ByteBuffer uBuff = MemoryUtil.memAlloc(materialNames.size() * Float.BYTES * 4);
		textureIDs = MemoryUtil.memAllocInt(materialNames.size());
		
		for(int i = 0; i < materialNames.size(); i++) {
			Material m = mtl.getMaterial(materialNames.get(i));
			
			uBuff.putFloat(m.colorDiffuse.x);
			uBuff.putFloat(m.colorDiffuse.y);
			uBuff.putFloat(m.colorDiffuse.z);
			uBuff.putFloat(1F);
			
			textureIDs.put(i);
		}
		uBuff.flip();
		textureIDs.flip();
		
		ubo.buffer(uBuff);
		ubo.loadData();
		
		if(programObj == null) {
			programObj = new ShaderProgram().gen();
			programObj.attachShader(ShaderLoader.SHADERS.get("shaders/obj.vs"));
			programObj.attachShader(ShaderLoader.SHADERS.get("shaders/obj.fs"));
			programObj.link();
		}
		GL.debug("ModelObj compile end");
	}
	
	public void render() {
		GL.debug("ModelObj render start");
		for(int i = 0; i < materialNames.size(); i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			mtl.getMaterial(materialNames.get(i)).textureDiffuse.bind();
		}
		
		programObj.bind();
		GL.debug("ModelObj program bind");
		vao.bind();
		GL.debug("ModelObj vao bind");
		
		GL20.glUniform1iv(0, textureIDs);
		GL.bindBufferBase(ubo, 4);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
		GL.debug("ModelObj render end");
	}
	
	public void setVertexInfo(int vertexCount, ByteBuffer positions, ByteBuffer normals, ByteBuffer uvs, ByteBuffer matIDs) {
		positionVBO = new VertexBufferObject();
		normalVBO = new VertexBufferObject();
		uvVBO = new VertexBufferObject();
		matidsVBO = new VertexBufferObject();
		
		positionVBO.buffer(positions);
		normalVBO.buffer(normals);
		uvVBO.buffer(uvs);
		matidsVBO.buffer(matIDs);
		
		this.vertexCount = vertexCount;
	}
	
	public void setMtlLibName(String name) {
		this.mtlName = name;
	}
	
	public void setMaterials(List<String> materials) {
		this.materialNames = materials;
	}
}

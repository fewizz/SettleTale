package ru.settletale.client.render;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.Texture2D;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.client.resource.ObjMtlLoader;
import ru.settletale.client.resource.ShaderLoader;

public class ObjModel {
	private String mtlName;
	private ObjMTL mtl;
	private List<String> materialsNames;
	private int vertexCount;
	private VertexArrayObject vao;
	private VertexBufferObject positionVBO;
	private VertexBufferObject normalVBO;
	private VertexBufferObject uvVBO;
	private VertexBufferObject matidsVBO;
	//private Texture2D colorsDiffuse;
	private int[] texUnitIDs;
	private Texture2D[] textures;
	
	static ShaderProgram programObj;
	
	public void compile() {
		GL.debug("ModelObj compile start");
		
		mtl = ObjMtlLoader.MTLS.get(mtlName);
		
		if(mtl == null) {
			System.out.println("Model is not using texture!");
			mtl = ObjMTL.DEFAULT;
			materialsNames.add("white");
		}
		
		positionVBO.gen().loadData();
		normalVBO.gen().loadData();
		matidsVBO.gen().loadData();
		uvVBO.gen().loadData();
		
		vao = new VertexArrayObject().gen();
		vao.vertexAttribPointer(positionVBO, 0, 4, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);
		
		vao.vertexAttribPointer(normalVBO, 1, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(1);
		
		vao.vertexAttribPointer(uvVBO, 2, 2, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(2);
		
		vao.vertexAttribPointer(matidsVBO, 3, 1, GL11.GL_BYTE, false, 0);
		vao.enableVertexAttribArray(3);
		
		texUnitIDs = new int[materialsNames.size()];
		textures = new Texture2D[materialsNames.size()];
		
		for(int i = 0; i < texUnitIDs.length; i++) {
			Texture2D tex = mtl.getMaterial(materialsNames.get(i)).textureDiffuse;
			
			textures[i] = tex;
			
			if(tex == null) {
				texUnitIDs[i] = -1;
				continue;
			}
			
			texUnitIDs[i] = i;
		}
		
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
		programObj.bind();
		GL.debug("ModelObj program bind");
		vao.bind();
		GL.debug("ModelObj vao bind");
		
		for(int i = 0; i < textures.length; i++) {
			Texture2D tex = textures[i];
			if(tex == null) {
				continue;
			}
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			tex.bind();
		}
		
		GL20.glUniform1iv(0, texUnitIDs);
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
		this.materialsNames = materials;
	}
}

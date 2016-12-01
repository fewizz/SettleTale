package ru.settletale.client.render.world;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import ru.settletale.client.opengl.BufferObject.Usage;
import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.Shader.Type;
import ru.settletale.world.biome.Biome;
import ru.settletale.world.region.Region;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.Texture1D;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.registry.Biomes;

public class CompiledRegion {
	Region region;
	VertexBufferObject vbo;
	VertexBufferObject cbo;
	VertexBufferObject nbo;
	VertexBufferObject idsbo;
	VertexBufferObject indxbo1;
	VertexBufferObject indxbo2;
	VertexArrayObject vao;
	static Texture1D biomeColorsTex;
	static ShaderProgram program = null;
	int vertsCount = 0;
	
	public CompiledRegion(Region region) {
		this.region = region;
		
		if(program == null) {
			GL.debug("CR shader start");
			program = new ShaderProgram().gen();
			program.attachShader(new Shader(Type.VERTEX, "shaders/terrain_vs.shader").gen().compile());
			program.attachShader(new Shader(Type.FRAGMENT, "shaders/terrain_fs.shader").gen().compile());
			program.link();
			GL.debug("CR shader end");
		}
		
		if(biomeColorsTex == null) {
			biomeColorsTex = new Texture1D(256).gen().setDefaultParams();
			FloatBuffer buff = BufferUtils.createFloatBuffer(256 * 4);
			
			int i = 0;
			for(Biome biome : Biomes.biomes) {
				if(biome == null) {
					break;
				}
				Color c = biome.color;
				float r = (float)c.getRed() / 255F;
				float g = (float)c.getGreen() / 255F;
				float b = (float)c.getBlue() / 255F;
				float a = 1F;
				
				buff.put(i + 0, r);
				buff.put(i + 1, g);
				buff.put(i + 2, b);
				buff.put(i + 3, a);
				i+=4;
			}
			
			biomeColorsTex.data(buff);
		}
	}
	
	public void compile(Region r, ByteBuffer poses, ByteBuffer idsMain, ByteBuffer normals, ByteBuffer ids1, ByteBuffer ids2) {
		GL.debug("CR compile start");
		
		vbo = new VertexBufferObject().gen();
		cbo = new VertexBufferObject().gen();
		nbo = new VertexBufferObject().gen();
		vao = new VertexArrayObject().gen();
		idsbo = new VertexBufferObject().gen();
		indxbo1 = new VertexBufferObject().gen();
		indxbo2 = new VertexBufferObject().gen();

		vbo.data(poses, Usage.STATIC_DRAW);
		cbo.data(idsMain, Usage.STATIC_DRAW);
		nbo.data(normals, Usage.STATIC_DRAW);
		indxbo1.data(ids1, Usage.STATIC_DRAW);
		indxbo2.data(ids2, Usage.STATIC_DRAW);
		
		vao.vertexAttribPointer(vbo, 0, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);
		
		vao.vertexAttribPointer(cbo, 1, 1, GL11.GL_UNSIGNED_BYTE, true, 0);
		vao.enableVertexAttribArray(1);
		
		vao.vertexAttribPointer(nbo, 2, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(2);
		
		vao.vertexAttribPointer(indxbo1, 3, 4, GL11.GL_UNSIGNED_BYTE, true, 0);
		vao.enableVertexAttribArray(3);
		
		vao.vertexAttribPointer(indxbo2, 4, 4, GL11.GL_UNSIGNED_BYTE, true, 0);
		vao.enableVertexAttribArray(4);
		
		vertsCount = poses.limit() / (3 * Float.BYTES);
		
		GL.debug("CR compile end");
	}
	
	public void render() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		biomeColorsTex.bind();
		GL.debug("CR bind vao");
		vao.bind();
		GL.debug("CR rend start");
		program.bind();
		GL.debug("CR rend shader start");
		GL11.glDrawArrays(GL11.GL_QUADS, 0, vertsCount);
		GL.debug("CR rend end");
		GL.debug("CR unbind vao");
	}
	
	public void clear() {
		vbo.delete();
		cbo.delete();
		nbo.delete();
		vao.delete();
		indxbo1.delete();
		indxbo2.delete();
	}
	
}

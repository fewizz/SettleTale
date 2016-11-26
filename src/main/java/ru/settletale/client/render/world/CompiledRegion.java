package ru.settletale.client.render.world;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.opengl.BufferObject.Usage;
import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.Shader.Type;
import ru.settletale.world.region.Region;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;

public class CompiledRegion {
	Region region;
	VertexBufferObject vbo;
	VertexBufferObject cbo;
	VertexBufferObject nbo;
	VertexArrayObject vao;
	public static ShaderProgram program = null;
	int vertsCount = 0;
	
	public CompiledRegion(Region r) {
		this.region = r;
		
		if(program == null) {
			GL.debug("CR shader start");
			program = new ShaderProgram().gen();
			program.attachShader(new Shader(Type.VERTEX, "shaders/terrain_vs.shader").gen().compile());
			program.attachShader(new Shader(Type.FRAGMENT, "shaders/terrain_fs.shader").gen().compile());
			program.link();
			GL.debug("CR shader end");
		}
	}
	
	public void compile(Region r, ByteBuffer poses, ByteBuffer colors, ByteBuffer normals) {
		GL.debug("CR compile start");
		
		vbo = new VertexBufferObject().gen();
		cbo = new VertexBufferObject().gen();
		nbo = new VertexBufferObject().gen();
		vao = new VertexArrayObject().gen();

		vbo.data(poses, Usage.STATIC_DRAW);
		cbo.data(colors, Usage.STATIC_DRAW);
		nbo.data(normals, Usage.STATIC_DRAW);
		
		vao.vertexAttribPointer(vbo, 0, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);
		
		vao.vertexAttribPointer(cbo, 1, 3, GL11.GL_UNSIGNED_BYTE, true, 0);
		vao.enableVertexAttribArray(1);
		
		vao.vertexAttribPointer(nbo, 2, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(2);
		
		vertsCount = poses.capacity() / 3;
		
		GL.debug("CR compile end");
	}
	
	public void render() {
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
	}
	
}

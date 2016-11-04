package ru.settletale.client.render.world;


import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.opengl.BufferObject;
import ru.settletale.client.opengl.OpenGL;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.Shader.Type;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.UniformBufferObject;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.world.Region;

public class CompiledRegion {
	Region region;
	VertexBufferObject vbo;
	VertexBufferObject cbo;
	VertexArrayObject vao;
	UniformBufferObject ubo;
	public static ShaderProgram program = null;
	int vertsCount = 0;
	
	public CompiledRegion(Region r) {
		this.region = r;
		
		if(program == null) {
			OpenGL.debug("CR shader start");
			program = new ShaderProgram(GL20.glCreateProgram());
			program.attachShader(Shader.gen(Type.VERTEX, "shaders/terrain_vs.shader").compile());
			program.attachShader(Shader.gen(Type.FRAGMENT, "shaders/terrain_fs.shader").compile());
			program.attachShader(Shader.gen(Type.GEOMETRY, "shaders/terrain_g.shader").compile());
			program.link();
			OpenGL.debug("CR shader end");
		}
	}
	
	public void compile(Region r, ByteBuffer poses, ByteBuffer colors) {
		OpenGL.debug("CR compile start");
		vbo = new VertexBufferObject().gen();
		cbo = new VertexBufferObject().gen();
		vao = new VertexArrayObject().gen();
		ubo = new UniformBufferObject().gen();
		vao.bind();
		
		//ubo.bind();
		//ubo.data(ByteBuffer.wrap(r.biomeIDs), Usage.STATIC_DRAW);
		//ubo.unbind();
		
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, OpenGL.uboMatricies.getID());
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 1, OpenGL.uboDisplaySize.getID());
		//GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 2, ubo.getID());

		vbo.data(poses, BufferObject.Usage.STATIC_DRAW);
		cbo.data(colors, BufferObject.Usage.STATIC_DRAW);
		
		vbo.bind();
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, MemoryUtil.NULL);
		GL20.glEnableVertexAttribArray(0);
		
		cbo.bind();
		GL20.glVertexAttribPointer(1, 3, GL11.GL_UNSIGNED_BYTE, true, 0, MemoryUtil.NULL);
		GL20.glEnableVertexAttribArray(1);
		
		cbo.unbind();
		
		vertsCount = poses.capacity() / 3;
		
		vao.unbind();
		OpenGL.debug("CR compile end");
	}
	
	public void render() {
		vao.bind();
		OpenGL.debug("CR rend start");
		program.bind();
		OpenGL.debug("CR rend shader start");
		GL11.glDrawArrays(GL11.GL_QUADS, 0, vertsCount);
		program.unbind();
		OpenGL.debug("CR rend end");
		vao.unbind();
		OpenGL.debug("CR unbind vao");
	}
	
	public void clear() {
		vbo.delete();
		cbo.delete();
		vao.delete();
	}
	
}

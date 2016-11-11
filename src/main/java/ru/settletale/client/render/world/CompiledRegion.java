package ru.settletale.client.render.world;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.opengl.BufferObject;
import ru.settletale.client.opengl.BufferObject.Usage;
import ru.settletale.client.opengl.OpenGL;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.Shader.Type;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.world.Region;

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
			OpenGL.debug("CR shader start");
			program = new ShaderProgram(GL20.glCreateProgram());
			program.attachShader(Shader.gen(Type.VERTEX, "shaders/terrain_vs.shader").compile());
			program.attachShader(Shader.gen(Type.FRAGMENT, "shaders/terrain_fs.shader").compile());
			program.link();
			OpenGL.debug("CR shader end");
		}
	}
	
	static Vector3f[] normalsArr = new Vector3f[33 * 33];
	static FloatBuffer normals = BufferUtils.createFloatBuffer(33 * 33 * 3 * 4);
	static Vector3f v1 = new Vector3f();
	static Vector3f v2 = new Vector3f();
	static Vector3f v3 = new Vector3f();
	static Vector3f n = new Vector3f();
	static Vector3f temp = new Vector3f();
	
	public void compile(Region r, ByteBuffer poses, ByteBuffer colors) {
		OpenGL.debug("CR compile start");
		vbo = new VertexBufferObject().gen();
		cbo = new VertexBufferObject().gen();
		nbo = new VertexBufferObject().gen();
		vao = new VertexArrayObject().gen();
		vao.bind();
		
		int i = 0;
		
		for(int x = 2; x < 35; x++) {
			for(int z = 2; z < 35; z++) {
				n.set(0, 0, 0);
				temp.set(0, 0, 0);
				
				v1.set(x, r.getHeight(x, z), z);
				
				v2.set(x, r.getHeight(x, z + 1), z + 1);
				v3.set(x + 1, r.getHeight(x + 1, z), z);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				temp.normalize();
				n.add(temp);
				
				v2.set(x + 1, r.getHeight(x + 1, z), z);
				v3.set(x, r.getHeight(x, z - 1), z - 1);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				temp.normalize();
				n.add(temp);
				
				v2.set(x, r.getHeight(x, z - 1), z - 1);
				v3.set(x - 1, r.getHeight(x - 1, z), z);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				temp.normalize();
				n.add(temp);
				
				v2.set(x - 1, r.getHeight(x - 1, z), z);
				v3.set(x, r.getHeight(x, z + 1), z + 1);
				v1.sub(v2, v2);
				v1.sub(v3, v3);
				v2.cross(v3, temp);
				temp.normalize();
				n.add(temp);
				
				n.normalize();
				
				normalsArr[i] = new Vector3f(n.x, n.y, n.z);
				i++;
			}
		}
		
		i = 0;
		
		for(int x1 = 0; x1 < 16; x1++) {
			for(int z1 = 0; z1 < 16; z1++) {
				for(int x2 = 0; x2 < 2; x2++) {
					for(int z2 = 0; z2 < 2; z2++) {
						int x = x1 * 2 + x2;
						int z = z1 * 2 + z2;
						
						Vector3f v1 = normalsArr[x * 33 + z];
						Vector3f v2 = normalsArr[x * 33 + z + 1];
						Vector3f v3 = normalsArr[(x + 1) * 33 + z + 1];
						Vector3f v4 = normalsArr[(x + 1) * 33 + z];
						
						normals.put(i, v1.x);
						i++;
						normals.put(i, v1.y);
						i++;
						normals.put(i, v1.z);
						i++;
						
						normals.put(i, v2.x);
						i++;
						normals.put(i, v2.y);
						i++;
						normals.put(i, v2.z);
						i++;
						
						normals.put(i, v3.x);
						i++;
						normals.put(i, v3.y);
						i++;
						normals.put(i, v3.z);
						i++;
						
						normals.put(i, v4.x);
						i++;
						normals.put(i, v4.y);
						i++;
						normals.put(i, v4.z);
						i++;
					}
				}
			}
		}
		
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, OpenGL.uboMatricies.getID());
		GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 1, OpenGL.uboDisplaySize.getID());

		vbo.data(poses, BufferObject.Usage.STATIC_DRAW);
		cbo.data(colors, BufferObject.Usage.STATIC_DRAW);
		nbo.data(normals, Usage.STATIC_DRAW);
		
		vbo.bind();
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, MemoryUtil.NULL);
		GL20.glEnableVertexAttribArray(0);
		
		cbo.bind();
		GL20.glVertexAttribPointer(1, 3, GL11.GL_UNSIGNED_BYTE, true, 0, MemoryUtil.NULL);
		GL20.glEnableVertexAttribArray(1);
		
		nbo.bind();
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 0, MemoryUtil.NULL);
		GL20.glEnableVertexAttribArray(2);
		
		nbo.unbind();
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
		OpenGL.debug("CR rend end");
		OpenGL.debug("CR unbind vao");
	}
	
	public void clear() {
		vbo.delete();
		cbo.delete();
		vao.delete();
	}
	
}

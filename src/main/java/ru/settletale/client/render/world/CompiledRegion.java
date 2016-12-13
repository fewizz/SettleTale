package ru.settletale.client.render.world;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.opengl.BufferObject.Usage;
import ru.settletale.client.opengl.ElementArrayBufferObject;
import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.Shader.Type;
import ru.settletale.client.resource.TextureLoader;
import ru.settletale.world.region.Region;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.Texture2D;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.client.vertex.PrimitiveArray;

public class CompiledRegion {
	Region region;
	VertexBufferObject pbo;
	VertexBufferObject nbo;

	ElementArrayBufferObject ib;

	VertexArrayObject vao;
	static Texture2D regionTexture;
	static ByteBuffer regionTextureTempBuffer = BufferUtils.createByteBuffer(18 * 18 * 4 * 4);
	static ShaderProgram program = null;
	int indexCount = 0;

	public CompiledRegion(Region region) {
		this.region = region;

		if (program == null) {
			GL.debug("CR shader start");
			program = new ShaderProgram().gen();
			program.attachShader(new Shader(Type.VERTEX, "shaders/terrain_vs.shader").gen().compile());
			program.attachShader(new Shader(Type.FRAGMENT, "shaders/terrain_fs.shader").gen().compile());
			program.link();
			GL.debug("CR shader end");
		}
		if(regionTexture == null) {
			regionTexture = TextureLoader.textures.get("textures\\grass.png");
		}
	}

	public void compile(PrimitiveArray pa) {
		GL.debug("CR compile start");

		pbo = new VertexBufferObject().gen();
		nbo = new VertexBufferObject().gen();

		ib = new ElementArrayBufferObject().gen();

		vao = new VertexArrayObject().gen();

		pbo.data(pa.getBuffer(WorldRenderer.POSITION), Usage.STATIC_DRAW);
		nbo.data(pa.getBuffer(WorldRenderer.NORMAL), Usage.STATIC_DRAW);

		ib.data(pa.getIndexBuffer(), Usage.STATIC_DRAW);

		vao.vertexAttribPointer(pbo, 0, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);

		vao.vertexAttribPointer(nbo, 1, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(1);

		indexCount = pa.getIndexCount();

		regionTexture = TextureLoader.textures.get("textures\\grass.png");/*new Texture2D(18, 18).gen();

		int i = 0;
		for (int z = -1; z < 17; z++) {
			for (int x = -1; x < 17; x++) {
				Color c = region.getBiome(x, z).color;

				regionTextureTempBuffer.put(i + 0, (byte) c.getRed());
				regionTextureTempBuffer.put(i + 1, (byte) c.getGreen());
				regionTextureTempBuffer.put(i + 2, (byte) c.getBlue());
				regionTextureTempBuffer.put(i + 3, (byte) 0xFF);

				i += 4;
			}
		}
		regionTexture.data(regionTextureTempBuffer);
		regionTexture.setDefaultParams();*/

		GL.debug("CR compile end");
	}

	public void render() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		regionTexture.bind();
		GL.debug("CR bind vao");
		vao.bind();
		GL.debug("CR rend start");
		program.bind();
		GL.debug("CR rend shader start");
		ib.bind();
		GL11.glDrawElements(GL11.GL_QUADS, indexCount, GL11.GL_UNSIGNED_SHORT, MemoryUtil.NULL);
		GL.debug("CR rend end");
		GL.debug("CR unbind vao");
	}

	public void clear() {
		pbo.delete();
		nbo.delete();
		vao.delete();
		ib.delete();
	}

}

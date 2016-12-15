package ru.settletale.client.render.world;

import java.awt.Color;
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
import ru.settletale.world.biome.Biome;
import ru.settletale.world.region.Region;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.Texture1D;
import ru.settletale.client.opengl.Texture2D;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.registry.Biomes;

public class CompiledRegion {
	Region region;
	VertexBufferObject pbo;
	VertexBufferObject nbo;

	ElementArrayBufferObject ib;

	VertexArrayObject vao;
	Texture2D textureIDs;
	static Texture1D textureBiomes;
	static Texture2D textureGrass;
	static ByteBuffer textureIDsTempBuffer = BufferUtils.createByteBuffer(18 * 18 * 5);
	static ShaderProgram program;
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
		if (textureGrass == null) {
			textureGrass = TextureLoader.textures.get("textures/grass.png");
		}
		if (textureBiomes == null) {
			textureBiomes = new Texture1D(256).gen().internalFormat(GL11.GL_RGB8).bufferFormat(GL11.GL_RGB).bufferType(GL11.GL_UNSIGNED_BYTE);

			ByteBuffer bb = BufferUtils.createByteBuffer(256 * 3);

			for (Biome b : Biomes.biomes) {
				if (b == null) {
					continue;
				}
				Color c = b.color;

				bb.put(b.getBiomeID() * 3 + 0, (byte) c.getRed());
				bb.put(b.getBiomeID() * 3 + 1, (byte) c.getGreen());
				bb.put(b.getBiomeID() * 3 + 2, (byte) c.getBlue());
			}

			textureBiomes.buffer = bb;
			textureBiomes.loadData();
		}
	}

	public void compile(PrimitiveArray pa) {
		GL.debug("CR compile start");

		pbo = new VertexBufferObject().gen();
		nbo = new VertexBufferObject().gen();

		ib = new ElementArrayBufferObject().gen();

		vao = new VertexArrayObject().gen();
		vao.bind();

		pbo.data(pa.getBuffer(WorldRenderer.POSITION), Usage.STATIC_DRAW);
		nbo.data(pa.getBuffer(WorldRenderer.NORMAL), Usage.STATIC_DRAW);

		ib.data(pa.getIndexBuffer(), Usage.STATIC_DRAW);

		vao.vertexAttribPointer(pbo, 0, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);

		vao.vertexAttribPointer(nbo, 1, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(1);
		vao.unbind();

		indexCount = pa.getIndexCount();

		textureIDs = new Texture2D(18, 18).gen().internalFormat(GL11.GL_RED).bufferFormat(GL11.GL_RED).bufferType(GL11.GL_UNSIGNED_BYTE);

		int i = 0;
		for (int z = -1; z < 17; z++) {
			for (int x = -1; x < 17; x++) {
				textureIDsTempBuffer.put(i + 0, (byte) region.getBiome(x, z).getBiomeID());
				i++;
			}
			i += 2; // WTF?
		}

		GL.debug("CR compile texture");
		textureIDs.buffer(textureIDsTempBuffer);
		textureIDs.loadData();

		GL.debug("CR compile end");
	}

	public void render() {
		program.bind();
		GL.debug("CR rend shader start");
		vao.bind();

		GL.debug("CR bind texture units start");
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		textureIDs.bind();
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		textureBiomes.bind();
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		textureGrass.bind();

		GL.debug("CR bind texture units end");

		GL.debug("CR rend start");
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
		textureIDs.delete();
	}

}

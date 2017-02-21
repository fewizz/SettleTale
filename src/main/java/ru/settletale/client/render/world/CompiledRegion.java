package ru.settletale.client.render.world;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture1D;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.render.RenderLayer;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.resource.TextureLoader;
import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.region.Region;
import ru.settletale.client.vertex.VertexArrayIndexed;
import ru.settletale.registry.Biomes;

public class CompiledRegion {
	static Texture1D textureBiomes;
	static Texture2D textureGrass;
	static final ByteBuffer textureIDsTempBuffer = BufferUtils.createByteBuffer(18 * 20);
	static final ShaderProgram program = new ShaderProgram();
	Region region;
	RenderLayer layer;
	Texture2D textureIDs;

	public CompiledRegion(Region region) {
		this.region = region;

		if (!program.isGenerated()) {
			GL.debug("CR shader start");
			program.gen();
			program.attachShader(ShaderLoader.SHADERS.get("shaders/terrain.vs"));
			program.attachShader(ShaderLoader.SHADERS.get("shaders/terrain.fs"));
			program.link();
			GL.debug("CR shader end");
		}
		if (textureGrass == null) {
			textureGrass = TextureLoader.TEXTURES.get("textures/grass.png");
			textureGrass.parameter(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			textureGrass.parameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		}
		if (textureBiomes == null) {
			textureBiomes = new Texture1D(256).gen().internalFormat(GL11.GL_RGB8).bufferFormat(GL11.GL_RGB).bufferType(GL11.GL_UNSIGNED_BYTE);

			ByteBuffer bb = BufferUtils.createByteBuffer(256 * 3);

			for (BiomeAbstract b : Biomes.BIOMES) {
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

	public void compile(VertexArrayIndexed pa) {
		GL.debug("CR compile start");

		this.layer = new RenderLayer(true);
		this.layer.setVertexArray(pa);
		this.layer.compile();
		this.layer.setShaderProgram(program);

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

		GL.debug("CR bind texture units start");
		GL.activeTexture(0, textureIDs);
		GL.activeTexture(1, textureBiomes);
		GL.activeTexture(2, textureGrass);

		GL.debug("CR bind texture units end");

		GL.debug("CR rend start");
		
		this.layer.render(GL11.GL_QUADS);
		GL.debug("CR rend end");
		GL.debug("CR unbind vao");
	}

	public void clear() {
		layer.getVertexArray().clear();
		textureIDs.delete();
	}

}

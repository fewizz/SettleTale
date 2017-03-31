package ru.settletale.client.render.world;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture1D;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.render.RenderLayer;
import ru.settletale.client.render.RenderLayerIndexed;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.resource.TextureLoader;
import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.region.Region;
import ru.settletale.client.vertex.VertexArrayDataBakerIndexed;
import ru.settletale.client.vertex.VertexAttribType;
import ru.settletale.registry.Biomes;

public class CompiledRegion {
	public static final int POSITION = 0;
	public static final int NORMAL = 1;
	public static final VertexArrayDataBakerIndexed VERTEX_ARRAY = new VertexArrayDataBakerIndexed(VertexAttribType.FLOAT_3, VertexAttribType.FLOAT_1);
	static final Texture1D TEXTURE_BIOMES = new Texture1D(256);
	static Texture2D textureGrass;
	static final ByteBuffer TEMP_BUFFER = MemoryUtil.memAlloc(18 * 20 * 2);
	static final ShaderProgram PROGRAM = new ShaderProgram();
	final Region region;
	RenderLayer layer;
	Texture2D textureIDs;

	public CompiledRegion(Region region) {
		this.region = region;
	}

	public void compile() {
		compileVertexAttributeArrays();
		
		if (!PROGRAM.isGenerated()) {
			GL.debug("CR shader start");
			PROGRAM.gen();
			PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/terrain.vs"));
			PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/terrain.fs"));
			PROGRAM.link();
			GL.debug("CR shader end");
		}
		if (textureGrass == null) {
			textureGrass = TextureLoader.TEXTURES.get("textures/grass.png");
			textureGrass.parameter(GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			textureGrass.parameter(GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		}
		if (!TEXTURE_BIOMES.isGenerated()) {
			TEXTURE_BIOMES.gen().format(GL_RGB8).bufferDataFormat(GL_RGB).bufferDataType(GL_UNSIGNED_BYTE);

			ByteBuffer buff = MemoryUtil.memAlloc(256 * 3);

			for (BiomeAbstract b : Biomes.BIOMES) {
				if (b == null) {
					continue;
				}
				Color c = b.color;

				buff.put(b.getBiomeID() * 3 + 0, (byte) c.getRed());
				buff.put(b.getBiomeID() * 3 + 1, (byte) c.getGreen());
				buff.put(b.getBiomeID() * 3 + 2, (byte) c.getBlue());
			}

			TEXTURE_BIOMES.loadData(buff);
		}

		GL.debug("CR compile start");

		this.layer = new RenderLayerIndexed();
		this.layer.setVertexArrayDataBaker(VERTEX_ARRAY);
		this.layer.compile();
		this.layer.setVertexArrayDataBaker(null);
		VERTEX_ARRAY.clearData();
		this.layer.setShaderProgram(PROGRAM);

		textureIDs = new Texture2D(18, 18).gen().format(GL30.GL_R8).bufferDataFormat(GL_RED).bufferDataType(GL_UNSIGNED_BYTE);

		int byteIndex = 0;
		for (int z = -1; z < 17; z++) {
			for (int x = -1; x < 17; x++) {
				TEMP_BUFFER.put(byteIndex++, (byte) region.getBiome(x, z).getBiomeID());
			}
			byteIndex += 2; // WTF? It's not RGB, but neeeds 3 bytes...
		}

		GL.debug("CR compile texture");
		textureIDs.loadData(TEMP_BUFFER);

		GL.debug("CR compile end");
	}

	public void render() {
		GL.debug("CR rend shader start");

		GL.activeTexture(0, textureIDs);
		GL.activeTexture(1, TEXTURE_BIOMES);
		GL.activeTexture(2, textureGrass);

		GL.debug("CR bind texture units end");

		this.layer.render(GL_QUADS);
		GL.debug("CR rend end");
	}
	
	static final Vector3f NORMAL_TEMP = new Vector3f();
	static final Vector3f TEMP = new Vector3f();
	static final Vector3f V1_TEMP = new Vector3f();
	static final Vector3f V2_TEMP = new Vector3f();
	static final Vector3f V3_TEMP = new Vector3f();
	
	private void compileVertexAttributeArrays() {
		GL.debug("Fill buffers0");

		for (int x = 0; x < 16; x++) {
			int nx = x * 2;

			for (int z = 0; z < 16; z++) {
				int nz = z * 2;

				int i1 = nx * 33 + nz;
				int i2 = i1 + 1;
				int i3 = i2 + 33;
				int i4 = i1 + 33;

				for (int x2 = 0; x2 < 2; x2++) {

					for (int z2 = 0; z2 < 2; z2++) {
						VERTEX_ARRAY.index(i1++);
						VERTEX_ARRAY.index(i2++);
						VERTEX_ARRAY.index(i3++);
						VERTEX_ARRAY.index(i4++);
					}

					i1 -= 2;
					i2 -= 2;
					i3 -= 2;
					i4 -= 2;

					i1 += 33;
					i2 += 33;
					i3 += 33;
					i4 += 33;
				}
			}
		}
		
		for (int x = 0; x < 33; x++) {
			int nx = x;
			float px = (nx / 2F);
			float pxf = region.x * 16 + px;
			float pzf = region.z * 16;

			for (int z = 0; z < 33; z++) {
				NORMAL_TEMP.set(0);
				TEMP.set(0);

				V1_TEMP.set(x, region.getHeight(x, z), z);

				V2_TEMP.set(x, region.getHeight(x, z + 1), z + 0.5F);
				V3_TEMP.set(x + 0.5F, region.getHeight(x + 1, z), z);
				V2_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x + 0.5F, region.getHeight(x + 1, z), z);
				V3_TEMP.set(x, region.getHeight(x, z - 1), z - 0.5F);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x, region.getHeight(x, z - 1), z - 0.5F);
				V3_TEMP.set(x - 0.5F, region.getHeight(x - 1, z), z);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x - 0.5F, region.getHeight(x - 1, z), z);
				V3_TEMP.set(x, region.getHeight(x, z + 1), z + 0.5F);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				NORMAL_TEMP.normalize();

				VERTEX_ARRAY.putFloat(POSITION, pxf, region.getHeight(x, z), pzf);
				VERTEX_ARRAY.putFloat(NORMAL, NORMAL_TEMP.y);

				VERTEX_ARRAY.endVertex();

				pzf += 0.5F;
			}
		}
	}

	public void clear() {
		textureIDs.delete();
	}

}

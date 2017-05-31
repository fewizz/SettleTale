package ru.settletale.client.render.world;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture1D;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.gl.Shader.Type;
import ru.settletale.client.render.RenderLayer;
import ru.settletale.client.render.RenderLayerIndexed;
import ru.settletale.client.resource.loader.ShaderSourceLoader;
import ru.settletale.client.resource.loader.TextureLoader;
import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.region.Region;
import ru.settletale.client.vertex.VertexArrayDataBakerIndexed;
import ru.settletale.client.vertex.VertexAttribType;
import ru.settletale.registry.Biomes;

public class CompiledRegion {
	public static final int POSITION = 0;
	public static final int NORMAL = 1;
	public static final VertexArrayDataBakerIndexed SHARED_VERTEX_ARRAY = new VertexArrayDataBakerIndexed(1024, 1024, true, VertexAttribType.FLOAT_3, VertexAttribType.FLOAT_1);
	static final Texture1D TEXTURE_BIOMES = new Texture1D(Region.WIDTH * Region.WIDTH);
	static Texture2D textureGrass;
	static final ByteBuffer TEMP_BUFFER = MemoryUtil.memAlloc(Region.WIDTH_EXTENDED * Region.WIDTH_EXTENDED * 2);
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	public boolean compiled = false;
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
			PROGRAM.attachShader(new Shader().gen(Type.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/terrain.vs")));
			PROGRAM.attachShader(new Shader().gen(Type.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/terrain.fs")));
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

			TEXTURE_BIOMES.data(buff);
		}

		GL.debug("CR compile start");

		this.layer = new RenderLayerIndexed();
		this.layer.setVertexArrayDataBaker(SHARED_VERTEX_ARRAY);
		this.layer.compile();
		this.layer.setVertexArrayDataBaker(null);
		SHARED_VERTEX_ARRAY.clear();
		this.layer.setShaderProgram(PROGRAM);

		textureIDs = new Texture2D(Region.WIDTH_EXTENDED, Region.WIDTH_EXTENDED).gen().format(GL30.GL_R8).bufferDataFormat(GL_RED).bufferDataType(GL_UNSIGNED_BYTE);

		int byteIndex = 0;
		for (int z = -Region.EXTENSION; z < Region.WIDTH + Region.EXTENSION; z++) {
			for (int x = -Region.EXTENSION; x < Region.WIDTH + Region.EXTENSION; x++) {
				TEMP_BUFFER.put(byteIndex++, (byte) region.getBiome(x, z).getBiomeID());
			}
			byteIndex += 2; // WTF? It's not RGB, but neeeds 3 bytes...
		}

		GL.debug("CR compile texture");
		textureIDs.data(TEMP_BUFFER);
		compiled = true;
		
		GL.debug("CR compile end");
	}

	public void render() {
		GL.debug("CR rend shader start");

		GL.bindTextureUnit(0, textureIDs);
		GL.bindTextureUnit(1, TEXTURE_BIOMES);
		GL.bindTextureUnit(2, textureGrass);

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
		int rendWidth = Region.WIDTH + 1;

		for (int x = 0; x < Region.WIDTH; x++) {
			for (int z = 0; z < Region.WIDTH; z++) {

				int i1 = x * rendWidth + z;
				int i2 = i1 + 1;
				int i3 = i2 + rendWidth;
				int i4 = i1 + rendWidth;
				SHARED_VERTEX_ARRAY.index(i1++);
				SHARED_VERTEX_ARRAY.index(i2++);
				SHARED_VERTEX_ARRAY.index(i3++);
				SHARED_VERTEX_ARRAY.index(i4++);
			}
		}

		for (int x = 0; x < rendWidth; x++) {
			float pxf = region.x * Region.WIDTH + x;
			float pzf = region.z * Region.WIDTH;

			for (int z = 0; z < rendWidth; z++) {
				NORMAL_TEMP.set(0);
				TEMP.set(0);

				V1_TEMP.set(x, region.getHeight(x, z), z);

				V2_TEMP.set(x, region.getHeight(x, z + 1), z + 1F);
				V3_TEMP.set(x + 1F, region.getHeight(x + 1, z), z);
				V2_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x + 1F, region.getHeight(x + 1, z), z);
				V3_TEMP.set(x, region.getHeight(x, z - 1), z - 1F);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x, region.getHeight(x, z - 1), z - 1F);
				V3_TEMP.set(x - 1F, region.getHeight(x - 1, z), z);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(x - 1F, region.getHeight(x - 1, z), z);
				V3_TEMP.set(x, region.getHeight(x, z + 1), z + 1F);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				NORMAL_TEMP.normalize();

				SHARED_VERTEX_ARRAY.putFloat(POSITION, pxf, region.getHeight(x, z), pzf);
				SHARED_VERTEX_ARRAY.putFloat(NORMAL, NORMAL_TEMP.y);

				SHARED_VERTEX_ARRAY.endVertex();

				pzf += 1F;
			}
		}
	}

	public void clear() {
		textureIDs.delete();
	}

}

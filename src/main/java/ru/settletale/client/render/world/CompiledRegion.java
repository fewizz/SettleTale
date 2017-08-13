package ru.settletale.client.render.world;

import java.awt.Color;
import java.nio.ByteBuffer;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.render.Renderer;
import ru.settletale.client.render.VertexAttribArray;
import ru.settletale.client.render.util.GLPrimitive;
import ru.settletale.client.render.util.GLUtils;
import ru.settletale.client.resource.Textures;
import ru.settletale.memory.MemoryBlock;
import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.region.Region;
import wrap.gl.ElementArrayBuffer;
import wrap.gl.GL;
import wrap.gl.ShaderProgram;
import wrap.gl.Texture1D;
import wrap.gl.Texture2D;
import wrap.gl.VertexArray;
import ru.settletale.registry.Biomes;

public class CompiledRegion {
	static final Texture1D TEXTURE_BIOMES = new Texture1D(Region.WIDTH * Region.WIDTH);
	static Texture2D textureGrass;
	static final ByteBuffer TEMP_BUFFER = MemoryUtil.memAlloc(Region.WIDTH_EXTENDED * Region.WIDTH_EXTENDED * 2);
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	public VertexArray vao = new VertexArray();
	public ElementArrayBuffer eabo = new ElementArrayBuffer();
	final Region region;
	Texture2D textureIDs;

	public CompiledRegion(Region region) {
		this.region = region;
	}

	public void compile() {
		vao.gen();
		eabo.gen();
		compileVertexAttributeArrays();

		if (!PROGRAM.isGenerated()) {
			Renderer.debugGL("CR shader start");
			
			GLUtils.linkShadersToProgram(PROGRAM, "shaders/terrain.vs", "shaders/terrain.fs");
			
			Renderer.debugGL("CR shader end");
		}
		if (textureGrass == null) {
			textureGrass = Textures.getOrLoad("textures/grass.png");
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

		Renderer.debugGL("CR compile start");

		textureIDs = new Texture2D(Region.WIDTH_EXTENDED, Region.WIDTH_EXTENDED).gen().format(GL30.GL_R8).bufferDataFormat(GL_RED).bufferDataType(GL_UNSIGNED_BYTE);

		int byteIndex = 0;
		for (int z = -Region.EXTENSION; z < Region.WIDTH + Region.EXTENSION; z++) {
			for (int x = -Region.EXTENSION; x < Region.WIDTH + Region.EXTENSION; x++) {
				TEMP_BUFFER.put(byteIndex++, (byte) region.getBiome(x, z).getBiomeID());
			}
			byteIndex += 2; // WTF? It's not RGB, but neeeds 3 bytes...
		}

		Renderer.debugGL("CR compile texture");
		textureIDs.data(TEMP_BUFFER);
		
		Renderer.debugGL("CR compile end");
	}

	public void render() {
		Renderer.debugGL("CR rend shader start");

		GL.bindTextureUnit(0, textureIDs);
		GL.bindTextureUnit(1, TEXTURE_BIOMES);
		GL.bindTextureUnit(2, textureGrass);

		Renderer.debugGL("CR bind texture units end");

		vao.bind();
		PROGRAM.use();
		
		eabo.bind();
		GL11.glDrawElements(GL_QUADS, Region.WIDTH * Region.WIDTH * 4, GL11.GL_UNSIGNED_SHORT, MemoryUtil.NULL);
		
		Renderer.debugGL("CR rend end");
	}

	static final Vector3f NORMAL_TEMP = new Vector3f();
	static final Vector3f TEMP = new Vector3f();
	static final Vector3f V1_TEMP = new Vector3f();
	static final Vector3f V2_TEMP = new Vector3f();
	static final Vector3f V3_TEMP = new Vector3f();

	private void compileVertexAttributeArrays() {
		MemoryBlock mbIndex = new MemoryBlock().allocateS(Region.WIDTH * Region.WIDTH * 4);
		VertexAttribArray attribPos = new VertexAttribArray(GLPrimitive.FLOAT, 3, (Region.WIDTH + 1) * (Region.WIDTH + 1));
		VertexAttribArray attribNormal = new VertexAttribArray(GLPrimitive.FLOAT, 1, (Region.WIDTH + 1) * (Region.WIDTH + 1));
		
		Renderer.debugGL("Fill buffers0");
		int rendWidth = Region.WIDTH + Region.EXTENSION;

		int i = 0;
		
		for (int x = 0; x < Region.WIDTH; x++) {
			short i1 = (short) (x * rendWidth + 0);
			short i2 = (short) (i1 + 1);
			short i3 = (short) (i2 + rendWidth);
			short i4 = (short) (i3 - 1);
			
			for (int z = 0; z < Region.WIDTH; z++) {
				mbIndex.putShortS(i++, i1++);
				mbIndex.putShortS(i++, i2++);
				mbIndex.putShortS(i++, i3++);
				mbIndex.putShortS(i++, i4++);
			}
		}

		i = 0;
		
		for (int xLocal = 0; xLocal < rendWidth; xLocal++) {
			for (int zLocal = 0; zLocal < rendWidth; zLocal++) {
				NORMAL_TEMP.set(0);
				TEMP.set(0);

				V1_TEMP.set(xLocal, region.getHeight(xLocal, zLocal), zLocal);

				V2_TEMP.set(xLocal, region.getHeight(xLocal, zLocal + 1), zLocal + 1F);
				V3_TEMP.set(xLocal + 1F, region.getHeight(xLocal + 1, zLocal), zLocal);
				V2_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(xLocal + 1F, region.getHeight(xLocal + 1, zLocal), zLocal);
				V3_TEMP.set(xLocal, region.getHeight(xLocal, zLocal - 1), zLocal - 1F);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(xLocal, region.getHeight(xLocal, zLocal - 1), zLocal - 1F);
				V3_TEMP.set(xLocal - 1F, region.getHeight(xLocal - 1, zLocal), zLocal);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				V2_TEMP.set(xLocal - 1F, region.getHeight(xLocal - 1, zLocal), zLocal);
				V3_TEMP.set(xLocal, region.getHeight(xLocal, zLocal + 1), zLocal + 1F);
				V1_TEMP.sub(V2_TEMP, V2_TEMP);
				V1_TEMP.sub(V3_TEMP, V3_TEMP);
				V2_TEMP.cross(V3_TEMP, TEMP);
				NORMAL_TEMP.add(TEMP);

				NORMAL_TEMP.normalize();

				attribPos.putFloatF(i * 3, region.x * Region.WIDTH + xLocal).putFloatF(i * 3 + 1, region.getHeight(xLocal, zLocal)).putFloatF(i * 3 + 2, region.z * Region.WIDTH + zLocal);
				attribNormal.putFloatF(i, NORMAL_TEMP.y);
				i++;
			}
		}
		
		vao.bind();
		eabo.bind();
		eabo.data(mbIndex.address(), mbIndex.bytes());
		
		attribPos.updateVertexBuffer();
		attribNormal.updateVertexBuffer();
		
		attribPos.pointToVAO(vao, 0, false);
		attribNormal.pointToVAO(vao, 1, false);
		
		mbIndex.free();
		attribPos.free();
		attribNormal.free();
	}

	public void clear() {
		textureIDs.delete();
	}

}

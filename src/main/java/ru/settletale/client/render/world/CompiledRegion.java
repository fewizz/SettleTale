package ru.settletale.client.render.world;

import java.awt.Color;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.ElementArrayBuffer;
import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture1D;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.gl.VertexArray;
import ru.settletale.client.render.Attrib;
import ru.settletale.client.render.Renderer;
import ru.settletale.client.render.VertexArrayBuilder;
import ru.settletale.client.render.VertexAttribArray;
import ru.settletale.client.render.util.GLPrimitive;
import ru.settletale.client.render.util.GLUtils;
import ru.settletale.client.resource.Textures;
import ru.settletale.memory.MemoryBlock;
import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.region.Chunk;
import ru.settletale.registry.Biomes;

public class CompiledRegion {
	static final Texture1D TEXTURE_BIOMES = new Texture1D(/*Chunk.WIDTH * Chunk.WIDTH*/);
	static Texture2D textureGrass;
	//static final ByteBuffer TEMP_BUFFER = MemoryUtil.memAlloc(Chunk.WIDTH_EXTENDED * Chunk.WIDTH_EXTENDED * 2);
	static final ShaderProgram PROGRAM = new ShaderProgram();
	
	final VertexArray vao = new VertexArray();
	ElementArrayBuffer eabo = new ElementArrayBuffer();
	final Chunk region;
	Texture2D textureIDs;

	public CompiledRegion(Chunk region) {
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
			TEXTURE_BIOMES.gen();

			MemoryBlock buff = new MemoryBlock(256 * 3);

			for (BiomeAbstract b : Biomes.BIOMES) {
				if (b == null) {
					continue;
				}
				Color c = b.color;

				buff.put(b.getBiomeID() * 3 + 0, (byte) c.getRed());
				buff.put(b.getBiomeID() * 3 + 1, (byte) c.getGreen());
				buff.put(b.getBiomeID() * 3 + 2, (byte) c.getBlue());
			}

			TEXTURE_BIOMES.data1D(buff, GL_RGB8, 256 * 3, GL_RGB, GL_UNSIGNED_BYTE);
			buff.free();
		}

		Renderer.debugGL("CR compile start");

		textureIDs = new Texture2D().gen();
		
		MemoryBlock mb = new MemoryBlock(Chunk.WIDTH_EXTENDED * (Chunk.WIDTH_EXTENDED + 2));

		int byteIndex = 0;
		for (int z = -Chunk.EXTENSION; z < Chunk.WIDTH + Chunk.EXTENSION; z++) {
			for (int x = -Chunk.EXTENSION; x < Chunk.WIDTH + Chunk.EXTENSION; x++) {
				mb.put(byteIndex++, (byte) region.getBiome(x, z).getBiomeID());
			}
			byteIndex += 2; // WTF? It's not RGB, but needs 3 bytes...
		}

		Renderer.debugGL("CR compile texture");
		textureIDs.data2D(mb, 0, GL30.GL_R8, Chunk.WIDTH_EXTENDED, Chunk.WIDTH_EXTENDED, GL_RED, GL_UNSIGNED_BYTE);
		mb.free();
		
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
		GL11.glDrawElements(GL_QUADS, Chunk.WIDTH * Chunk.WIDTH * 4, GL11.GL_UNSIGNED_SHORT, MemoryUtil.NULL);
		
		Renderer.debugGL("CR rend end");
	}

	static final Vector3f NORMAL_TEMP = new Vector3f();
	static final Vector3f TEMP = new Vector3f();
	static final Vector3f V1_TEMP = new Vector3f();
	static final Vector3f V2_TEMP = new Vector3f();
	static final Vector3f V3_TEMP = new Vector3f();

	private void compileVertexAttributeArrays() {
		MemoryBlock mbIndex = new MemoryBlock().allocateS(Chunk.WIDTH * Chunk.WIDTH * 4);
		VertexArrayBuilder vaoBuilder = new VertexArrayBuilder((Chunk.WIDTH + 1) * (Chunk.WIDTH + 1), Attrib.floatType(0, GLPrimitive.FLOAT, 3, false), Attrib.floatType(1, GLPrimitive.FLOAT, 1, false));
		//VertexAttribArrayBuilder attribPos = new VertexAttribArrayBuilder(GLPrimitive.FLOAT, 3, (Chunk.WIDTH + 1) * (Chunk.WIDTH + 1));
		//VertexAttribArrayBuilder attribNormal = new VertexAttribArrayBuilder(GLPrimitive.FLOAT, 1, (Chunk.WIDTH + 1) * (Chunk.WIDTH + 1));
		
		Renderer.debugGL("Fill buffers0");
		int rendWidth = Chunk.WIDTH + Chunk.EXTENSION;

		int i = 0;
		
		for (int x = 0; x < Chunk.WIDTH; x++) {
			short i1 = (short) (x * rendWidth + 0);
			short i2 = (short) (i1 + 1);
			short i3 = (short) (i2 + rendWidth);
			short i4 = (short) (i3 - 1);
			
			for (int z = 0; z < Chunk.WIDTH; z++) {
				mbIndex.putShortS(i++, i1++);
				mbIndex.putShortS(i++, i2++);
				mbIndex.putShortS(i++, i3++);
				mbIndex.putShortS(i++, i4++);
			}
		}

		i = 0;
		Renderer.debugGL("end indexes");
		
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

				vaoBuilder.float3(0, region.x * Chunk.WIDTH + xLocal, region.getHeight(xLocal, zLocal), region.z * Chunk.WIDTH + zLocal);
				vaoBuilder.float1(1, NORMAL_TEMP.y);
				vaoBuilder.endVertex();
				//attribPos.putFloatF(i * 3, region.x * Chunk.WIDTH + xLocal).putFloatF(i * 3 + 1, region.getHeight(xLocal, zLocal)).putFloatF(i * 3 + 2, region.z * Chunk.WIDTH + zLocal);
				//attribNormal.putFloatF(i, NORMAL_TEMP.y);
				i++;
				//System.out.println(i);
			}
		}
		
		Renderer.debugGL("post compile");
		
		vao.bind();
		eabo.bind();
		eabo.data(mbIndex.address(), mbIndex.bytes());
		mbIndex.free();
		
		Renderer.debugGL("CR preVaoBuild");
		
		vaoBuilder.build(vao);
		
		/*attribPos.updateVertexBuffer();
		attribNormal.updateVertexBuffer();
		
		vao.vertexAttribPointer(attribPos, 0, false);
		vao.vertexAttribPointer(attribNormal, 1, false);
		
		mbIndex.free();
		attribPos.free();
		attribNormal.free();*/
	}

	public void clear() {
		textureIDs.delete();
	}

}

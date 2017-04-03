package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;

import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Texture;
import ru.settletale.client.vertex.VertexAttribType;

public class RenderLayerTextured extends RenderLayer {
	final HashObjIntMap<Texture<?>> textures;

	public RenderLayerTextured(VertexAttribType... storages) {
		super(storages);
		textures = HashObjIntMaps.newMutableMap();
	}

	public int setTexture(Texture<?> texture) {
		if (!textures.containsKey(texture)) {
			int id = getFreeTextureID();
			textures.put(texture, getFreeTextureID());
			return id;
		}
		return textures.getInt(texture);
	}

	public int setTexture(Texture<?> texture, int id) {
		if (textures.containsKey(texture)) {
			throw new Error("Already exists");
		}
		return textures.put(texture, id);
	}

	public int getTextureID(Texture<?> tex) {
		return textures.getInt(tex);
	}

	public int getFreeTextureID() {
		return textures.size();
	}

	public boolean isTextureUsed(Texture<?> tex) {
		return textures.containsKey(tex);
	}

	@Override
	public void render(int mode) {
		program.bind();
		vao.bind();

		textures.forEach((Texture<?> texture, int id) -> {
			GL.setActiveTexture(id, texture);
		});

		GL11.glDrawArrays(mode, 0, vertexCount);
	}

	public void clearTextures() {
		textures.clear();
	}

	public int getUsedTextureCount() {
		return textures.size();
	}
}

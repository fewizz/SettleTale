package ru.settletale.client.render;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.resource.TextureLoader;
import ru.settletale.client.vertex.VertexAttribType;

public class RenderLayerTextured extends RenderLayer {
	private UniformType uniformType = null;
	private int textureAttribIndex = -1;
	private int textureUniformLocation = -1;
	private final Texture2D[] textures;
	private IntBuffer textureIDs;
	private int textureCount = 0;

	public RenderLayerTextured(VertexAttribType... storages) {
		super(storages);
		textures = new Texture2D[256];
	}

	public void setTexture(Texture2D texture) {
		if (textureAttribIndex == -1)
			throw new RuntimeException("textureAttributeIndex not specified");

		for (int id = 0; id < textures.length; id++) {
			if (textures[id] == null) { // Not used yet
				textures[id] = texture;
				textureCount++;
			}

			if (textures[id] != texture)
				continue;

			getVertexArrayDataBaker().putByte(textureAttribIndex, (byte) id);
			return;
		}

		throw new Error("Too many used textures");
	}

	@Override
	public void compile() {
		if (textureAttribIndex == -1)
			throw new RuntimeException("textureAttributeIndex not specified");

		super.compile();

		if (uniformType == null) { // If without textures
			vao.disableVertexAttribArray(textureAttribIndex);
			return;
		}

		textureIDs = MemoryUtil.memAllocInt(textureCount);

		for (int i = 0; i < textureCount; i++) {
			textureIDs.put(i, i);
		}
	}

	@Override
	public void render(int mode) {
		program.bind();
		vao.bind();

		if (uniformType == UniformType.INT_ARRAY) {
			for (int i = 0; i < textureCount; i++) {
				GL.activeTexture(i, textures[i]);
			}

			GL20.glUniform1iv(textureUniformLocation, textureIDs);
		}
		if (uniformType == UniformType.INT) {
			GL.activeTexture(0, textures[0]);
			GL20.glUniform1i(textureUniformLocation, 0);
		}

		GL11.glDrawArrays(mode, 0, vertexCount);
	}

	public void setTextureAttribIndex(int index) {
		this.textureAttribIndex = index;
	}

	public void setTextureUniformLocation(int location) {
		this.textureUniformLocation = location;
	}

	public void setUniformType(UniformType uniformType) {
		this.uniformType = uniformType;
	}

	public void setEmptyTexture() {
		setTexture(TextureLoader.TEXTURES.get("textures/white.png"));
	}

	public void clearTextures() {
		for (int i = 0; i < textureCount; i++) {
			textures[i] = null;
		}

		textureCount = 0;
	}

	public int getUsedTextureCount() {
		return textureCount;
	}

	@Override
	public void delete() {
		super.delete();
		
		if (textureIDs != null)
			MemoryUtil.memFree(textureIDs);
	}
}

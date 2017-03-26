package ru.settletale.client.render;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.resource.TextureLoader;
import ru.settletale.client.vertex.AttributeType;

public class RenderLayerTextured extends RenderLayer {
	TextureUniformType uniformType;
	int textureAttributeIndex = -1;
	int textureUniformLocation = -1;
	final Texture2D[] textures;
	IntBuffer textureIDs;
	int textureCount = 0;

	public RenderLayerTextured(AttributeType... storages) {
		super(storages);
		textures = new Texture2D[256];
	}

	public void setTextureAttributeIndex(int index) {
		this.textureAttributeIndex = index;
	}

	public void setTextureUniformLocation(int location) {
		this.textureUniformLocation = location;
	}

	public void setUniformType(TextureUniformType uniformType) {
		this.uniformType = uniformType;
	}

	public void setTexture(Texture2D texture) {
		int id = 0;

		for (int i = 0; i < textures.length; i++) {
			boolean nul = textures[i] == null;
			
			if (textures[i] == texture || nul) {
				id = i;
				textures[i] = texture;

				if (nul) {
					textureCount++;
				}
				break;
			}
		}

		getVertexAttributeArray().dataByte(textureAttributeIndex, (byte)id);
	}

	public int getUsedTextureCount() {
		return textureCount;
	}

	@Override
	public void compile(boolean allowSubDataIfPossible) {
		super.compile(allowSubDataIfPossible);

		if(uniformType == TextureUniformType.NONE) {
			vao.disableVertexAttribArray(textureAttributeIndex);
			return;
		}
		textureIDs = MemoryUtil.memAllocInt(textureCount);

		for (int i = 0; i < textureCount; i++) {
			textureIDs.put(i, i);
		}
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

	@Override
	public void render(int mode) {
		program.bind();
		vao.bind();

		if (uniformType == TextureUniformType.ARRAY) {
			for (int i = 0; i < textureCount; i++) {
				GL.activeTexture(i, textures[i]);
			}

			GL20.glUniform1iv(textureUniformLocation, textureIDs);
		}
		if(uniformType == TextureUniformType.VARIABLE) {
			GL.activeTexture(0, textures[0]);
			GL20.glUniform1i(textureUniformLocation, 0);
		}

		GL11.glDrawArrays(mode, 0, vertexCount);
	}

	public static enum TextureUniformType {
		ARRAY,
		VARIABLE,
		NONE
	}
}

package ru.settletale.client.render;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture;

public class TextureUnitBinder {
	final List<Texture<?>> textures = new ArrayList<>();

	public void bind() {
		for (int index = 0; index < textures.size(); index++) {
			GL.bindTextureUnit(index, textures.get(index));
		}
	}

	public int use(Texture<?> texture) {
		int index = textures.indexOf(texture);
		if(index == -1) {
			textures.add(texture);
			index = textures.indexOf(texture);
		}
		return index;
	}

	public boolean isContains(Texture<?> texture) {
		return textures.contains(texture);
	}

	public int getCount() {
		return textures.size();
	}

	public void setForIntArrayUniform(ShaderProgram program, int location) {
		try (MemoryStack ms = MemoryStack.stackPush()) {
			IntBuffer buff = ms.mallocInt(textures.size());

			for (int i = 0; i < textures.size(); i++) {
				buff.put(i, i);
			}

			program.setUniformIntArray(location, buff);
		}
	}

	public void clear() {
		textures.clear();
	}
}

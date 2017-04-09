package ru.settletale.client.render;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;

import com.carrotsearch.hppc.IntArrayList;
import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture;

/** Group = uniform**/
public class TextureUnitBinder {
	int currentUniformLocation = -1;
	IntArrayList currentGroup;
	/** All used texture units **/
	final List<Texture<?>> textures = new ArrayList<>();
	/** Array of texture unit indexes **/
	final HashIntObjMap<IntArrayList> groups = HashIntObjMaps.newMutableMap();

	public void setCurrentUniformLocation(int location) {
		currentUniformLocation = location;
		currentGroup = groups.get(location);
		if(currentGroup == null) {
			currentGroup = new IntArrayList();
			groups.put(location, currentGroup);
		}
	}

	public void bind() {
		for (int index = 0; index < textures.size(); index++) {
			GL.bindTextureUnit(index, textures.get(index));
		}
	}

	public int use(Texture<?> texture) {
		if(currentUniformLocation == -1) {
			throw new Error("Uniform location not set");
		}
		int unitIndex = textures.indexOf(texture);
		if (unitIndex == -1) {
			textures.add(texture);
			unitIndex = textures.indexOf(texture);
		}

		if (!currentGroup.contains(unitIndex)) {
			currentGroup.add(unitIndex);
		}
		return currentGroup.indexOf(unitIndex);
	}

	public void updateUniforms(ShaderProgram program) {
		program.bind();
		
		groups.forEach((int location, IntArrayList group) -> {
			if(group.size() <= 1) {
				return;
			}
			
			try (MemoryStack ms = MemoryStack.stackPush()) {
				IntBuffer buff = ms.mallocInt(group.size());

				for (int i = 0; i < group.size(); i++) {
					buff.put(i, group.get(i));
				}

				program.setUniformIntArray(location, buff);
			}
		});
		
		GL.debug("Texture Unit Binder updateUniforms end");
	}
	
	public int getUsedTextureUnitCount() {
		return textures.size();
	}

	public void clear() {
		textures.clear();
		groups.forEach((int location, IntArrayList group) -> {
			group.clear();
		});
		groups.clear();
	}
}

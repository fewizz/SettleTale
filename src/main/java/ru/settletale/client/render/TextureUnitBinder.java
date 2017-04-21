package ru.settletale.client.render;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryStack;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture;
import ru.settletale.util.AdvancedArrayList;
import ru.settletale.util.AdvancedList;
import ru.settletale.util.IndexArray;

/** Group = uniform**/
public class TextureUnitBinder {
	int currentUniformLocation = -1;
	IndexArray currentIndexArray;
	/** All used texture units **/
	final AdvancedList<Texture<?>> textureUnits = new AdvancedArrayList<>();
	/** Array of texture unit indexes **/
	final HashIntObjMap<IndexArray> arrays = HashIntObjMaps.newMutableMap();

	public void setCurrentUniformLocation(int location) {
		if (currentUniformLocation == location) {
			return;
		}

		currentUniformLocation = location;

		arrays.computeIfAbsent(location, key -> new IndexArray());
		currentIndexArray = arrays.get(location);

	}

	public void bindTextures() {
		textureUnits.forEachIndexed((index, tex) -> GL.bindTextureUnit(index, tex));
	}

	/** Returns position in int array uniform **/
	public int use(Texture<?> texture) {
		textureUnits.addIfAbsent(texture);
		int unitIndex = textureUnits.indexOf(texture);
		int position = currentIndexArray.setNextFreePositionIfAbsent(unitIndex);
		currentIndexArray.set(position, unitIndex);
		return position;
	}

	public void use(Texture<?> texture, int position) {
		if (currentUniformLocation == -1) {
			throw new Error("Uniform location not set");
		}

		textureUnits.addIfAbsent(texture);
		currentIndexArray.set(position, textureUnits.indexOf(texture));
	}

	public void updateUniforms(ShaderProgram program) {
		arrays.forEach((int location, IndexArray array) -> {
			if (array.getSize() == 0) {
				return;
			}

			try (MemoryStack ms = MemoryStack.stackPush()) {
				IntBuffer buff = ms.mallocInt(array.getSize());

				array.forEach((position, index) -> buff.put(position, index));

				program.setUniformIntArray(location, buff);
			}
		});

		GL.debug("Texture Unit Binder updateUniforms end");
	}

	public int getUsedTextureUnitCount() {
		return textureUnits.size();
	}

	public void clear() {
		textureUnits.clear();

		arrays.forEach((int location, IndexArray array) -> {
			array.clear();
		});
	}
}

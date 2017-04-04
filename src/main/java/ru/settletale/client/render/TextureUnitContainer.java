package ru.settletale.client.render;

import java.util.function.ObjIntConsumer;

import com.koloboke.collect.map.hash.HashObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Texture;

public class TextureUnitContainer {
	final HashObjIntMap<Texture<?>> textures = HashObjIntMaps.newMutableMap();
	int maxIndex = -1;
	int minIndex = -1;
	
	public void bind() {
		textures.forEach((Texture<?> texture, int index) -> {
			GL.bindTextureUnit(index, texture);
		});
	}
	
	public int add(Texture<?> texture) {
		int index = getFreeIndex();
		set(texture, index);
		return index;
	}
	
	public void remove(Texture<?> texture) {
		int index = getIndex(texture);
		if(index == maxIndex ) {
			maxIndex--;
		}
		if(index == minIndex) {
			minIndex--;
		}
		textures.removeAsInt(texture);
	}

	public void set(Texture<?> texture, int index) {
		if(index > maxIndex) {
			maxIndex = index;
		}
		if(index == minIndex + 1) {
			minIndex++;
		}
		textures.put(texture, index);
	}

	public int getIndex(Texture<?> texture) {
		if(!textures.containsKey(texture)) {
			return -1;
		}
		return textures.getInt(texture);
	}

	public int getFreeIndex() {
		for(int index = minIndex; index < maxIndex; index++) {
			if(!textures.containsValue(index)) {
				return index;
			}
		}
		return maxIndex + 1;
	}

	public boolean isContains(Texture<?> tex) {
		return textures.containsKey(tex);
	}
	
	public void forEach(ObjIntConsumer<? super Texture<?>> action) {
		textures.forEach(action);
	}
	
	public int getAmount() {
		return textures.size();
	}
	
	public void clear() {
		textures.clear();
		maxIndex = -1;
		minIndex = -1;
	}
}

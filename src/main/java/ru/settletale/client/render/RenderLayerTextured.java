package ru.settletale.client.render;

import com.koloboke.collect.map.hash.HashIntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Texture;
import ru.settletale.util.AdvancedArrayList;
import ru.settletale.util.AdvancedList;

public class RenderLayerTextured extends RenderLayer {
	final AdvancedList<Texture<?>> textureUnits = new AdvancedArrayList<>();
	final HashIntObjMap<HashIntObjMap<Texture<?>>> textures = HashIntObjMaps.newMutableMap();
	
	public void register(int location, int index, Texture<?> texture) {
		textureUnits.addIfAbsent(texture);
		textures.computeIfAbsent(location, key -> HashIntObjMaps.newMutableMap());
		textures.get(location).put(index, texture);
	}
	
	@Override
	public void render(int mode) {
		textureUnits.forEachIndexed((int index, Texture<?> t) -> {
			GL.bindTextureUnit(index, t);
		});
		
		
		
		super.render(mode);
	}
}

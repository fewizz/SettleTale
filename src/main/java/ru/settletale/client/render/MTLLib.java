package ru.settletale.client.render;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.gl.Texture;

public class MTLLib {
	private final Map<String, Material> materials = new HashMap<>();
	private final Map<Material, Texture<?>> textures = new HashMap<>();
	
	public Material getMaterial(String name) {
		return materials.get(name);
	}
	
	public void addMaterial(Material mat, String name) {
		materials.put(name, mat);
	}
	
	public void addTextureToMaterial(Material m, Texture<?> tex) { 
		textures.put(m, tex);
	}
	
	public Texture<?> getMaterialTexture(Material m) {
		return textures.get(m);
	}
}

package ru.settletale.client.render;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.gl.Texture;

public class MTLLib {
	private final Map<String, Material> materials = new HashMap<>();
	private final Map<Material, Texture<?>> diffuseTextures = new HashMap<>();
	private final Map<Material, Texture<?>> bumpTextures = new HashMap<>();
	
	public Material getMaterial(String name) {
		return materials.get(name);
	}
	
	public void addMaterial(Material mat, String name) {
		materials.put(name, mat);
	}
	
	public void addDiffuseTextureToMaterial(Material m, Texture<?> tex) { 
		diffuseTextures.put(m, tex);
	}
	
	public void addBumpTextureToMaterial(Material m, Texture<?> tex) { 
		bumpTextures.put(m, tex);
	}
	
	public Texture<?> getDiffuseTexture(Material m) {
		return diffuseTextures.get(m);
	}
	
	public Texture<?> getBumpTexture(Material m) {
		return bumpTextures.get(m);
	}
}

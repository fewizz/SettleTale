package ru.settletale.client.render;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.gl.Texture;

public class MTLLib {
	private final Map<String, Material> materials = new HashMap<>();
	private final Map<Material, Texture<?>> textures = new HashMap<>();
	
	public void compile() {
		
		/*for(Material m : materials.values()) {
			m.textureDiffuse = TextureLoader.TEXTURES.get(m.texturePathDiffuse);
			
			if(m.textureDiffuse == null) {
				System.out.println("Not found: " + m.texturePathDiffuse);
				m.textureDiffuse = TextureLoader.TEXTURES.get("textures/white.png");
			}
		}*/
	}
	
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

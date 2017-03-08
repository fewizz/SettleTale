package ru.settletale.client.render;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.resource.TextureLoader;

public class MTLLib {
	public final Map<String, Material> materials = new HashMap<>();
	public static final MTLLib DEFAULT = new MTLLib();
	
	static {
		Material m = new Material();
		m.name = "white";
		m.textureDiffuse = TextureLoader.TEXTURES.get("textures/white.png");
		
		DEFAULT.addMaterial(m);
	}
	
	public void compile() {
		
		for(Material m : materials.values()) {
			m.textureDiffuse = TextureLoader.TEXTURES.get(m.texturePathDiffuse);
			
			if(m.textureDiffuse == null) {
				System.out.println("Not found: " + m.texturePathDiffuse);
				m.textureDiffuse = TextureLoader.TEXTURES.get("textures/white.png");
			}
		}
	}
	
	public Material getMaterial(String name) {
		return materials.get(name);
	}
	
	public void addMaterial(Material mat) {
		materials.put(mat.name, mat);
	}
}

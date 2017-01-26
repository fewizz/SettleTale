package ru.settletale.client.render;

import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3f;

import ru.settletale.client.opengl.Texture2D;
import ru.settletale.client.resource.TextureLoader;

public class ObjMTL {
	public final Map<String, Material> materials = new HashMap<>();
	public static final ObjMTL DEFAULT = new ObjMTL();
	
	static {
		Material m = new Material();
		m.name = "white";
		m.textureDiffuse = TextureLoader.TEXTURES.get("textures/white.png");
		
		DEFAULT.addMaterial(m);
	}
	
	public Material getMaterial(String name) {
		return materials.get(name);
	}
	
	public void addMaterial(Material mat) {
		materials.put(mat.name, mat);
	}
	
	public static class Material {
		public String name;
		public String textureNameDiffuse;
		public Vector3f colorDiffuse;
		public Texture2D textureDiffuse;
	}
}

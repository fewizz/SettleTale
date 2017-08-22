package ru.settletale.client.resource;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.gl.Texture2D;

public class Textures {
	public static final Map<String, Texture2D> MAP = new HashMap<>();
	
	public static Texture2D getOrLoad(String key) {
		synchronized (MAP) {
			Texture2D t = MAP.get(key);
			
			if(t == null) {
				t = ResourceManager.TEX_LOADER.loadResource(key);
				MAP.put(key, t);
			}
			
			return t;
		}
 	}
	
	
}

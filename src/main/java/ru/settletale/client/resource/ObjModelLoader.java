package ru.settletale.client.resource;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.render.ModelObj;

public class ObjModelLoader extends ResourceLoaderOneExtAbstract {
	static final Map<String, ModelObj> MODELS = new HashMap<>();

	@Override
	public String getRequiredExtension() {
		return "obj";
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		
	}
	
}

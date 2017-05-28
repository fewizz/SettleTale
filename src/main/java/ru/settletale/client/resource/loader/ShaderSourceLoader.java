package ru.settletale.client.resource.loader;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.resource.ResourceFile;
import ru.settletale.util.FileUtils;

public class ShaderSourceLoader extends ResourceLoaderAbstract {
	public static final Map<String, String> SHADER_SOURCES = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "vs", "fs", "glsl" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading shader: " + resourceFile.key);

		String source = FileUtils.readWholeLines(resourceFile.path.toFile());

		SHADER_SOURCES.put(resourceFile.key, source);
	}
}

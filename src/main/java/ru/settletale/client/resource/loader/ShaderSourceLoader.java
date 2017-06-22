package ru.settletale.client.resource.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.settletale.client.resource.ResourceFile;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.util.FileUtils;
import ru.settletale.util.StringUtils;

public class ShaderSourceLoader extends ResourceLoaderAbstract {
	public static final Map<String, String> SHADER_SOURCES = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "vs", "fs", "glsl" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading shader: " + resourceFile.key);

		List<String> sourceLines = FileUtils.readLines(resourceFile.path.toFile());

		for(int i = 0; i < sourceLines.size(); i++) {
			String str = sourceLines.get(i);
			
			if(str.isEmpty() || str.charAt(0) != '#') {
				continue;
			}
			
			String[] strs = str.split(" ");
			
			if(strs[0].startsWith("#include")) {
				String libPath = strs[1];
				
				if(!SHADER_SOURCES.containsKey(libPath)) {
					ResourceManager.loadResource(libPath);
				}
				
				String libSource = SHADER_SOURCES.get(libPath);
				sourceLines.set(i, libSource);
			}
		}
		
		SHADER_SOURCES.put(resourceFile.key, StringUtils.concatAll(sourceLines));
	}
}

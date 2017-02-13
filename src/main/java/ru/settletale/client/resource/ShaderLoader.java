package ru.settletale.client.resource;

import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.gl.Shader;
import ru.settletale.client.render.GLThread;
import ru.settletale.util.FileUtils;

public class ShaderLoader extends ResourceLoaderAbstract {
	public static final Map<String, Shader> SHADERS = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] {"vs", "fs"};
	}
	
	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading shader: " + resourceFile.key);
		
		String source = FileUtils.readWholeLines(resourceFile.fullPath);
		
		Shader.Type type = Shader.Type.getByExtension(resourceFile.getExtension());
		Shader shader = new Shader(type, source);
		
		GLThread.addTask(() -> {
			shader.gen().compile();
		});
		
		SHADERS.put(resourceFile.key, shader);
	}
}

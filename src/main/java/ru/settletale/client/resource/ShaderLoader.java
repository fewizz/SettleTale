package ru.settletale.client.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.opengl.Shader;
import ru.settletale.client.render.GLThread;

public class ShaderLoader extends ResourceLoaderAbstract {
	public static final Map<String, Shader> SHADERS = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] {"vs", "fs"};
	}
	
	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading shader: " + resourceFile.key);
		
		StringBuilder text = new StringBuilder();
		
		try(FileReader fr = new FileReader(resourceFile.fullPath); BufferedReader reader = new BufferedReader(fr)) {
			for (;;) {
				String line = reader.readLine();

				if (line == null) {
					break;
				}

				text.append(line).append("\n");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Shader.Type type = Shader.Type.getByExtension(resourceFile.getExtension());
		Shader shader = new Shader(type, text.toString());
		
		GLThread.addTask(() -> {
			shader.gen().compile();
		});
		
		SHADERS.put(resourceFile.key, shader);
	}
}

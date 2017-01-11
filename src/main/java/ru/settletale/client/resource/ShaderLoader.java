package ru.settletale.client.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShaderLoader extends ResourceLoaderAbstract {
	public static final Map<String, String> shaderSources = new HashMap<>();

	@Override
	public String getRequiredExtension() {
		return "shader";
	}
	
	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading shader: " + resourceFile.key);
		
		try(FileReader fr = new FileReader(resourceFile.fullPath); BufferedReader reader = new BufferedReader(fr)) {
			StringBuilder text = new StringBuilder();

			for (;;) {
				String line = null;
				line = reader.readLine();

				if (line == null) {
					break;
				}

				text.append(line).append("\n");
			}

			shaderSources.put(resourceFile.key, text.toString());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}

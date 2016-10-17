package ru.settletale.client.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShaderLoader {
	public static final Map<String, String> shaderSources = new HashMap<>();
	
	public static void loadShader(String id, Path path) {
		System.out.println("Loading shader: " + id);
		Path pathFull = new File(path.toString() + "/" + id).toPath();
		
		List<String> list = new ArrayList<>();
		
		try {
			list = Files.lines(pathFull).collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringBuilder text = new StringBuilder();
		
		for(String line : list) {
			text.append(line).append("\n");
		}
		
		shaderSources.put(id.replace("\\", "/"), text.toString());
	}
}

package ru.settletale.client.resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ru.settletale.client.resource.loader.FontLoader;
import ru.settletale.client.resource.loader.ShaderSourceLoader;
import ru.settletale.client.resource.loader.TextureLoader;

public class ResourceManager {
	public static final TextureLoader TEX_LOADER = new TextureLoader();
	public static final ShaderSourceLoader SHADER_SOURCE_LOADER = new ShaderSourceLoader();
	public static final FontLoader FONT_LOADER = new FontLoader();
	
	public static final ResourceDirectory ROOT = new ResourceDirectory(null, "assets");

	public static void scanResourceFiles() {
		addScanFolder(Paths.get("assets/"));
		addScanFolder(Paths.get("src/main/resources/assets/"));
		ROOT.scan();
	}

	private static void addScanFolder(Path path) {
		if(!Files.exists(path) || !Files.isDirectory(path) || ROOT.paths.contains(path)) {
			return;
		}
		
		ROOT.addPath(path);
	}
}

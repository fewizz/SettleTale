package ru.settletale.client.resource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceManager {
	static final List<ResourceLoaderAbstract> RESOURCE_LOADERS = new ArrayList<>();
	static final Map<String, ResourceFile> KEY_RES_MAP = new HashMap<>();
	static final List<ResourceDirectory> ROOTS = new ArrayList<>();

	public static void loadResources() {
		RESOURCE_LOADERS.add(new TextureLoader());
		RESOURCE_LOADERS.add(new ShaderLoader());
		RESOURCE_LOADERS.add(new FontLoader());
		RESOURCE_LOADERS.add(new ObjModelLoader());
		RESOURCE_LOADERS.add(new MtlLibLoader());

		startResourceScanning();
		
		ROOTS.forEach(root -> {
			root.loadResources();
		});
	}
	
	private static void startResourceScanning() {
		scanFolder(new File("assets/").toPath());
		scanFolder(new File("src/main/resources/assets/").toPath());
	}

	public static void loadResource(ResourceFile res) {
		if (res.isLoaded()) {
			return;
		}

		ResourceManager.RESOURCE_LOADERS.forEach(loader -> {
			for (String ext : loader.getRequiredExtensions()) {
				if (!res.isExtensionEqual(ext))
					continue;
				
				loader.loadResource(res);
			}
		});
		
		res.setLoaded(true);
		KEY_RES_MAP.put(res.key, res);
	}

	private static void scanFolder(Path path) {
		if(!Files.exists(path) || !Files.isDirectory(path)) {
			return;
		}
		
		ResourceDirectory root = new ResourceDirectory(null, path);
		ROOTS.add(root);
		root.scan();
	}
}

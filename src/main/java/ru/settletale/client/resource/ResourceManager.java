package ru.settletale.client.resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ResourceManager {
	static final List<ResourceLoaderAbstract> RESOURCE_LOADERS = new ArrayList<>();
	static final Map<String, ResourceFile> KEY_LOADED_RES_MAP = new HashMap<>();
	static final List<ResourceDirectory> ROOTS = new ArrayList<>();
	static final Queue<Runnable> TASKS = new ConcurrentLinkedQueue<>();

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
		
		TASKS.forEach(run -> run.run());
	}
	
	private static void startResourceScanning() {
		scanFolder(Paths.get("assets/"));
		scanFolder(Paths.get("src/main/resources/assets/"));
	}

	public static void loadResource(String resPath) {
		for(ResourceDirectory dir : ROOTS) {
			ResourceFile file = dir.getResourceFileIncludingSubdirectories(resPath);
			
			if(file != null) {
				loadResource(file);
				return;
			}
		}
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
		KEY_LOADED_RES_MAP.put(res.key, res);
	}

	private static void scanFolder(Path path) {
		if(!Files.exists(path) || !Files.isDirectory(path)) {
			return;
		}
		
		ResourceDirectory root = new ResourceDirectory(null, path);
		ROOTS.add(root);
		root.scan();
	}
	
	public static boolean isResourceLoaded(String resPath) {
		return KEY_LOADED_RES_MAP.containsKey(resPath);
	}
	
	public static boolean isResourceLoaded(ResourceFile res) {
		return KEY_LOADED_RES_MAP.containsValue(res);
	}
	
	public static void runAfterResourceLoaded(Runnable run) {
		TASKS.add(run);
	}
}

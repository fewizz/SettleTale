package ru.settletale.client.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import ru.settletale.event.Event;
import ru.settletale.event.EventManager;

public class ResourceManager {
	static final Map<String, ResourceFile> resourceFiles = new HashMap<>();
	static final List<ResourceLoaderAbstract> resourceLoaders = new ArrayList<>();

	public static void loadResources() {
		resourceLoaders.add(new TextureLoader());
		resourceLoaders.add(new ShaderLoader());
		resourceLoaders.add(new FontLoader());
		resourceLoaders.add(new ObjModelLoader());
		resourceLoaders.add(new MtlLibLoader());

		resourceLoaders.forEach(rla -> rla.onResourcesLoadStart());
		startResourceScanning();
		resourceFiles.forEach((key, resourceFile) -> resourceLoaders.forEach(rla -> {
			for(String ext : rla.getRequiredExtensions()) {
				if (resourceFile.isEqualExtension(ext)) {
					rla.loadResource(resourceFile);
				}
			}
		}));
		resourceLoaders.forEach(rla -> rla.onResourcesLoadEnd());
		
		EventManager.fireEvent(Event.RESOURCE_MANAGER_LOADED);
	}

	private static void startResourceScanning() {
		scanFolder(new File("assets/"));
		scanFolder(new File("src/main/resources/assets/"));
	}

	private static void scanFolder(File file) {
		if (!file.exists() || !file.isDirectory()) {
			return;
		}

		Path assetsPath = file.toPath().toAbsolutePath();
		int assetsIndex = assetsPath.getNameCount();

		try (Stream<Path> paths = Files.walk(assetsPath, FileVisitOption.FOLLOW_LINKS);) {

			for (Iterator<Path> it = paths.iterator(); it.hasNext();) {
				Path path = it.next();

				if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
					continue;
				}

				String key = path.subpath(assetsIndex, path.getNameCount()).toString();
				key = key.replace("\\", "/");

				resourceFiles.put(key, new ResourceFile(key, path.toAbsolutePath().toFile()));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ResourceFile getResourceFile(String key) {
		return resourceFiles.get(key);
	}
}

package ru.settletale.client.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import ru.settletale.event.Event;
import ru.settletale.event.EventManager;

public class ResourceManager {
	static final List<ResourceFile> resourceFiles = new ArrayList<>();
	static final List<ResourceLoaderAbstract> resourceLoaders = new ArrayList<>();

	public static void loadResources() {
		resourceLoaders.add(new TextureLoader());
		resourceLoaders.add(new ShaderLoader());
		resourceLoaders.add(new FontLoader());

		resourceLoaders.forEach(rla -> rla.onResourceManagerStart());
		startResourceScanning();
		resourceFiles.forEach(resourceFile -> resourceLoaders.forEach(rla -> {
			for(String ext : rla.getRequiredExtensions()) {
				if (resourceFile.isEqualExtension(ext)) {
					rla.loadResource(resourceFile);
				}
			}
		}));
		resourceLoaders.forEach(rla -> rla.onResourcesLoaded());
		
		EventManager.fireEvent(Event.ResourceManagerLoaded);
	}

	private static void startResourceScanning() {
		scanFolder(new File("assets/"));
		scanFolder(new File("bin/assets/"));
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

				resourceFiles.add(new ResourceFile(key, path.toAbsolutePath().toFile()));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

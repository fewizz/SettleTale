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

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class ResourceManager {
	static List<ResourceFile> resourceFiles;
	static List<ResourceLoaderAbstract> resourceLoaders;

	public static void init() {
		resourceFiles = new ArrayList<>();
		resourceLoaders = new ArrayList<>();
		
		resourceLoaders.add(new TextureLoader());
		resourceLoaders.add(new ShaderLoader());
		resourceLoaders.add(new FontLoader());

		resourceLoaders.forEach((ResourceLoaderAbstract rla) -> rla.onResourceManagerStart());
		startResourceScanning();
		resourceFiles.forEach(resourceFile -> resourceLoaders.forEach(rla -> {
			if(resourceFile.isEqualExtension(rla.getRequiredExtension())) {
				rla.loadResource(resourceFile);
			}
		}));
		resourceLoaders.forEach((ResourceLoaderAbstract rla) -> rla.onResourcesLoadedPre());
		resourceLoaders.forEach((ResourceLoaderAbstract rla) -> rla.onResourcesLoadedPost());
	}

	static void startResourceScanning() {
		File main = new File("assets/");
		if (main.exists()) {
			scanFolder(main);
		}

		FastClasspathScanner scanner = new FastClasspathScanner();
		List<File> files = scanner.getUniqueClasspathElements();

		for (File file : files) {
			if (!file.exists() || !file.isDirectory()) {
				continue;
			}

			File fileAssets = new File(file.getAbsolutePath(), "/assets/");

			if (!fileAssets.exists()) {
				continue;
			}

			scanFolder(fileAssets);
		}
	}

	private static void scanFolder(File file) {
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

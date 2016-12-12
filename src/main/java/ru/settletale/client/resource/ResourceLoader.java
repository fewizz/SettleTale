package ru.settletale.client.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import ru.settletale.client.PlatformClient;

public class ResourceLoader {

	public static void init() {
		File main = new File("assets/");
		if(main.exists()) {
			walkInAssetsFolder(main);
		}
		
		FastClasspathScanner scanner = new FastClasspathScanner();
		List<File> files = scanner.getUniqueClasspathElements();

		for (File file : files) {
			if (!file.exists() || !file.isDirectory()) {
				continue;
			}

			File fileAssets = new File(file.getAbsolutePath(), "/assets");

			if (!fileAssets.exists()) {
				continue;
			}

			walkInAssetsFolder(fileAssets);
		}
		
		PlatformClient.runInRenderThread(new Runnable() {
			@Override
			public void run() {
				TextureLoader.registerTextures();
			}
		});
	}

	private static void walkInAssetsFolder(File file) {
		Path assetsPath = file.toPath();
		assetsPath = assetsPath.toAbsolutePath();
		int index = assetsPath.getNameCount();

		try (Stream<Path> paths = Files.walk(assetsPath, FileVisitOption.FOLLOW_LINKS);) {

			for (Iterator<Path> it = paths.iterator(); it.hasNext();) {
				Path path = it.next();

				if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
					continue;
				}
	
				String id = path.subpath(index, path.getNameCount()).toString();

				if (id.endsWith(".png")) {
					TextureLoader.loadTexture(id, assetsPath);
				}
				if (id.endsWith(".shader")) {
					ShaderLoader.loadShader(id, assetsPath);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

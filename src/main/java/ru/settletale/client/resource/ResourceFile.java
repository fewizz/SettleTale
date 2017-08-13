package ru.settletale.client.resource;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.commons.io.FilenameUtils;

public class ResourceFile {
	public final String key;
	public final String name;
	public final Queue<Path> paths = new ArrayDeque<>();
	public final ResourceDirectory dir;

	public ResourceFile(ResourceDirectory dir, String name) {
		this.dir = dir;
		this.name = name;
		key = dir.key + "/" + name;
	}
	
	public void addPath(Path p) {
		this.paths.add(p);
	}

	public boolean isExtensionEqual(String extension) {
		return getExtension().equals(extension);
	}
	
	public Path getLeadPath() {
		return paths.peek();
	}

	public String getExtension() {
		return FilenameUtils.getExtension(name);
	}
}

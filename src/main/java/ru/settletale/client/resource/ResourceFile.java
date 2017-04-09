package ru.settletale.client.resource;

import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;

public class ResourceFile {
	boolean loaded = false;
	final String key;
	final String name;
	final public Path path;
	final public ResourceDirectory dir;

	public ResourceFile(ResourceDirectory dir, Path path) {
		this.path = path;
		this.dir = dir;
		name = path.getFileName().toString();
		key = dir.key + name;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public void setLoaded(boolean b) {
		this.loaded = b;
	}

	public boolean isExtensionEqual(String extension) {
		return getExtension().equals(extension);
	}

	public String getExtension() {
		return FilenameUtils.getExtension(path.toString());
	}
}

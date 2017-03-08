package ru.settletale.client.resource;

import java.io.File;
import java.nio.file.Path;

public class ResourceFile {
	public final String key;
	public final File fullPath;
	public final File subPath;
	
	public ResourceFile(String key, Path fullPath, Path subPath) {
		this.key = key;
		this.fullPath = fullPath.toFile();
		this.subPath = subPath.toFile();
	}
	
	public boolean isEqualExtension(String extension) {
		return key.endsWith("." + extension);
	}
	
	public String getExtension() {
		return key.substring(key.lastIndexOf(".") + 1);
	}
}

package ru.settletale.client.resource;

import java.io.File;

public class ResourceFile {
	public final String key;
	public final File fullPath;
	
	public ResourceFile(String key, File fullPath) {
		this.key = key;
		this.fullPath = fullPath;
	}
	
	public boolean isEqualExtension(String extension) {
		return key.endsWith(extension);
	}
	
	public String getExtension() {
		return key.substring(key.lastIndexOf(".") + 1);
	}
}

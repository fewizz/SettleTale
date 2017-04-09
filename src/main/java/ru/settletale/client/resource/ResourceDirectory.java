package ru.settletale.client.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class ResourceDirectory {
	final String key;
	final String name;
	final ResourceDirectory prevDir;
	final ArrayList<ResourceFile> resources = new ArrayList<>();
	final ArrayList<ResourceDirectory> directories = new ArrayList<>();
	final Path path;

	public ResourceDirectory(ResourceDirectory prevDir, Path path) {
		this.path = path;
		this.prevDir = prevDir;
		name = path.getFileName().toString();
		key = isRoot() ? "" : prevDir.key + name + "/";
	}

	public void scan() {
		System.out.println(path.toAbsolutePath());
		
		try {
			Files.list(path).forEach(otherPath -> {
				if (Files.isRegularFile(otherPath)) {
					resources.add(new ResourceFile(this, otherPath));
				}

				if (Files.isDirectory(otherPath)) {
					directories.add(new ResourceDirectory(this, otherPath));
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		directories.forEach(dir -> dir.scan());
	}

	public void loadResources() {
		resources.forEach(res -> {
			ResourceManager.loadResource(res);
		});

		directories.forEach(dir -> {
			dir.loadResources();
		});
	}
	
	public ResourceFile getResourceFile(String path) {
		path = path.replace('\\', '/');
		
		ResourceDirectory dir = null;
		
		if(path.contains("/")) {
			dir = getResourceDirectory(path);
		}
		else {
			dir = this;
		}
		
		for(ResourceFile res : dir.resources) {
			if(res.name.equals(path)) {
				return res;
			}
		}
		
		return null;
	}
	
	public ResourceDirectory getResourceDirectory(String path) {
		path = path.replace('\\', '/');
		
		for(ResourceDirectory dir : directories) {
			if(path.startsWith(dir.name)) {
				path = path.substring(name.length());
				
				if(path.contains("/")) {
					return dir.getResourceDirectory(path);
				}
				else {
					return dir;
				}
			}
		}
		
		return null;
	}
	
	public boolean isRoot() {
		return prevDir == null;
	}
}

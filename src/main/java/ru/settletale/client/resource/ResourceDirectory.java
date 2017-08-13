package ru.settletale.client.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

public class ResourceDirectory {
	final String key;
	final String name;
	final ResourceDirectory parentDir;
	final Set<ResourceFile> files = new HashSet<>();
	final Set<ResourceDirectory> directories = new HashSet<>();
	final Queue<Path> paths = new ArrayDeque<>();

	public ResourceDirectory(ResourceDirectory parentDir, String name) {
		this.parentDir = parentDir;
		this.name = FilenameUtils.normalizeNoEndSeparator(name);
		key = isRoot() ? "" : (parentDir.isRoot() ? "" : parentDir.key + "/") + name;
	}
	
	public void addPath(Path p) {
		paths.add(p);
	}

	public void scan() {
		try {
			for(Path path : paths) {
				Files.list(path).forEach(childPath -> {
					
					if (Files.isRegularFile(childPath)) {
						String name = childPath.getFileName().toString();
						ResourceFile file = getFile(name);
						
						if(file == null) {
							file = new ResourceFile(this, name);
							files.add(file);
						}
						
						file.addPath(childPath);
					}

					if (Files.isDirectory(childPath)) {
						String name = childPath.getFileName().toString();
						ResourceDirectory dir = getDirectory(name);
						
						if(dir == null) {
							dir = new ResourceDirectory(this, name);
							directories.add(dir);
						}
						
						dir.paths.add(childPath);
					}
				
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		directories.forEach(dir -> dir.scan());
	}
	
	public void forEachFile(IResourceFileIter func, String... extensions) {
		files.forEach(file ->  {
			if(extensions.length == 0)
				func.iter(file);
			else {
				for(String ext : extensions) {
					if(file.getExtension().equals(ext)) {
						func.iter(file);
						break;
					}
				}
			}
		});
	}
	
	public void forEachFileIncludingSubdirectories(IResourceFileIter func, String... extensions) {
		forEachFile(func, extensions);
		directories.forEach(dir -> dir.forEachFileIncludingSubdirectories(func, extensions));
	}
	
	@FunctionalInterface
	public interface IResourceFileIter {
		public void iter(ResourceFile file);
	}
	
	public ResourceFile getFile(String name) {
		for(ResourceFile file : files) {
			if(file.name.equals(name))
				return file;
		}
		return null;
	}
	
	public ResourceDirectory getDirectory(String name) {
		for(ResourceDirectory dir : directories) {
			if(dir.name.equals(name))
				return dir;
		}
		return null;
	}

	public ResourceFile findFileIncludingSubdirectories(String path) {
		path = path.replace('\\', '/');

		ResourceDirectory dir = null;

		if (path.contains("/")) {
			dir = findDirectoryIncludingSubdirectories(path);
		}
		else
			dir = this;

		return dir.getFile(FilenameUtils.getName(path));
	}

	public ResourceDirectory findDirectoryIncludingSubdirectories(String path) {
		path = path.replace('\\', '/');
		
		boolean isFile = !FilenameUtils.getExtension(path).equals("");
		
		if(isFile) {
			path = path.substring(0, path.length() - FilenameUtils.getName(path).length() - 1);
		}
		System.out.println(path);
		
		int indexOfSlash = path.indexOf('/');
		
		if(indexOfSlash == -1) {
			return getDirectory(path);
		}
		
		String childPath = path.substring(0, indexOfSlash);
		return getDirectory(childPath).findDirectoryIncludingSubdirectories(path.substring(indexOfSlash + 1, path.length()));
	}

	public boolean isRoot() {
		return parentDir == null;
	}
}

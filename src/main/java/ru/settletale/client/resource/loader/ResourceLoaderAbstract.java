package ru.settletale.client.resource.loader;

import ru.settletale.client.resource.ResourceFile;
import ru.settletale.client.resource.ResourceManager;

public abstract class ResourceLoaderAbstract<T> {
	
	public abstract T loadResource(ResourceFile resourceFile);
	
	public T loadResource(String res) {
		return loadResource(ResourceManager.ROOT.findFileIncludingSubdirectories(res));
	}
}

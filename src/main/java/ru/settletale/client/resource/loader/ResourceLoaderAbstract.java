package ru.settletale.client.resource.loader;

import ru.settletale.client.resource.ResourceFile;

public abstract class ResourceLoaderAbstract {
	public abstract String[] getRequiredExtensions();
	public abstract void loadResource(ResourceFile resourceFile);
}

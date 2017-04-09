package ru.settletale.client.resource;

public abstract class ResourceLoaderAbstract {
	public abstract void loadResource(ResourceFile resourceFile);
	public abstract String[] getRequiredExtensions();
}

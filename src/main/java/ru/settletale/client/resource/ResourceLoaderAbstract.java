package ru.settletale.client.resource;

public abstract class ResourceLoaderAbstract {
	public void onResourceManagerStart() {}
	public void onResourcesLoaded() {}
	public abstract void loadResource(ResourceFile resourceFile);
	public abstract String[] getRequiredExtensions();
}

package ru.settletale.client.resource;

public abstract class ResourceLoaderAbstract {
	public abstract String getRequiredExtension();
	public void onResourceManagerStart() {}
	public void onResourcesLoaded() {}
	public abstract void loadResource(ResourceFile resourceFile);
}

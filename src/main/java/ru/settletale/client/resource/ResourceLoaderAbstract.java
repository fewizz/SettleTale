package ru.settletale.client.resource;

public abstract class ResourceLoaderAbstract {
	public void onResourcesLoadStart() {}
	public void onResourcesLoadEnd() {}
	public abstract void loadResource(ResourceFile resourceFile);
	public abstract String[] getRequiredExtensions();
}

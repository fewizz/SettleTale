package ru.settletale.client.resource;

public abstract class ResourceLoaderOneExtAbstract extends ResourceLoaderAbstract {
	public abstract String getRequiredExtension();
	
	public final String[] getRequiredExtensions() {
		return new String[] {getRequiredExtension()};
	}
}

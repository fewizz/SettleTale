package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

public class Accessor {
	ColladaArray source;
	public final int count;
	public final int stride;

	public Accessor(Source parentSource, Element accessorElement) {
		source = parentSource.array;
		count = Integer.parseInt(accessorElement.getAttribute("count"));
		stride = Integer.parseInt(accessorElement.getAttribute("stride"));
	}
}

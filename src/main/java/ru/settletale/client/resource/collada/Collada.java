package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Collada {
	final Element colladaElement;
	public LibraryGeometries geometries;
	
	public Collada(Element colladaElement) {
		this.colladaElement = colladaElement;
	}
	
	public Collada loadGeometries() {
		Element elementGeometries = XMLUtils.getFirstChildElement("library_geometries", colladaElement);
		geometries = new LibraryGeometries(elementGeometries);
		return this;
	}
}

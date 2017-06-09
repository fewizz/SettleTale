package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Collada {
	public final Asset asset;
	public LibraryGeometries geometries;
	public LibraryVisualScenes visualScenes;
	
	public Collada(Element colladaElement) {
		asset = new Asset(XMLUtils.getFirstChildElement("asset", colladaElement));
		
		geometries = new LibraryGeometries(XMLUtils.getFirstChildElement("library_geometries", colladaElement));
		
		visualScenes = new LibraryVisualScenes(XMLUtils.getFirstChildElement("library_visual_scenes", colladaElement));
	}
}

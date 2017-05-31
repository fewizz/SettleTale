package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class LibraryGeometries {
	public final List<Geometry> geometries;
	
	public LibraryGeometries(Element elementGeometries) {
		geometries = new ArrayList<>();
		
		XMLUtils.forEachChildElementWithName("geometry", elementGeometries, element -> {
			Geometry geometry = new Geometry(element);
			geometries.add(geometry);
		});
	}
}

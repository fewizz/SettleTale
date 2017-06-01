package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class LibraryGeometries {
	public final List<Geometry> geometriesList;
	
	public LibraryGeometries(Element elementGeometries) {
		geometriesList = new ArrayList<>();
		
		XMLUtils.forEachChildElementWithName("geometry", elementGeometries, element -> {
			Geometry geometry = new Geometry(element);
			geometriesList.add(geometry);
		});
	}
}

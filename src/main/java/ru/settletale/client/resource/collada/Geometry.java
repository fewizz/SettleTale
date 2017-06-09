package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Geometry extends ObjectWithIDAndName {
	public final Mesh mesh;

	public Geometry(Element geometryElement) {
		super(geometryElement);
		
		mesh = new Mesh(XMLUtils.getFirstChildElement("mesh", geometryElement));
	}
}

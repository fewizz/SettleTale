package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

public abstract class ColladaElementWithID {
	public final String id;
	
	public ColladaElementWithID(Element element) {
		id = element.getAttribute("id");
	}
}

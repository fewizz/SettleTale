package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

public class ColladaElementWithIDAndName extends ColladaElementWithID {
	public final String name;
	
	public ColladaElementWithIDAndName(Element element) {
		super(element);
		name = element.getAttribute("name");
	}

}

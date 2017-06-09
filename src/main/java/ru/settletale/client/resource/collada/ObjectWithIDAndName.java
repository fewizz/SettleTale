package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

public class ObjectWithIDAndName extends ObjectWithID {
	public final String name;
	
	public ObjectWithIDAndName(Element element) {
		super(element);
		name = element.getAttribute("name");
	}

}

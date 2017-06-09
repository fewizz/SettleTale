package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

public abstract class ObjectWithID {
	public final String id;
	
	public ObjectWithID(Element element) {
		id = element.getAttribute("id");
	}
}

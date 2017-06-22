package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

public abstract class TransformationElement {
	public final String sid;
	
	public TransformationElement(Element element) {
		sid = element.getAttribute("sid");
	}

}

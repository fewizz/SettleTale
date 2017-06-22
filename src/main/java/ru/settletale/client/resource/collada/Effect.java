package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Effect extends ObjectWithID {
	public final Phong phong;

	public Effect(Element element) {
		super(element);
		
		phong = new Phong(XMLUtils.getFirstChildElement("phong", XMLUtils.getFirstChildElement("technique", XMLUtils.getFirstChildElement("profile_COMMON", element))));
	}

}

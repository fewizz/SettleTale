package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Material extends ObjectWithIDAndName {
	public final Effect effect;

	public Material(Collada c, Element element) {
		super(element);
		
		effect = c.getEffectByURL(XMLUtils.getFirstChildElement("instance_effect", element).getAttribute("url"));
	}

}

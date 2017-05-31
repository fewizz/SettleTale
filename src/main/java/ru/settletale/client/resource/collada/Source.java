package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Source extends ColladaElementWithID {
	public final ColladaArray array;
	public final Accessor accessor;

	public Source(Element sourceElement) {
		super(sourceElement);
		
		array = new FloatArray(XMLUtils.getFirstChildElement("float_array", sourceElement));
		accessor = new Accessor(this, XMLUtils.getFirstChildElement("accessor", XMLUtils.getFirstChildElement("technique_common", sourceElement)));
	}
}

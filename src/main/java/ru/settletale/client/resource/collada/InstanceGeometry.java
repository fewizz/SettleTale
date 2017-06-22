package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class InstanceGeometry {
	public final Geometry geometry;
	public final Material material;
	
	public InstanceGeometry(Collada c, Element el) {
		geometry = c.getGeometryByURL(el.getAttribute("url"));
		material = c.getMaterialByURL(XMLUtils.getFirstChildElement("instance_material", XMLUtils.getFirstChildElement("technique_common", XMLUtils.getFirstChildElement("bind_material", el)))
				.getAttribute("target"));
	}
}

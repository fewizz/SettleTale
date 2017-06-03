package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Vertices extends ColladaElementWithID {
	public final Source source;
	public final Input input;
	public final Mesh mesh;

	public Vertices(Mesh mesh, Element element) {
		super(element);
		
		this.mesh = mesh;
		input = new Input(XMLUtils.getFirstChildElement("input", element));
		source = mesh.getSource(input.source);
	}
}

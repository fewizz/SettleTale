package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class VisualScene extends ObjectWithIDAndName {
	public final List<Node> nodes;
	
	public VisualScene(Collada c, Element el) {
		super(el);
		
		nodes = new ArrayList<>();
		XMLUtils.forEachChildElementWithName("node", el, nodeEl -> nodes.add(new Node(c, nodeEl)));
	}
}

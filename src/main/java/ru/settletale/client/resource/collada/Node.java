package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Node extends ObjectWithIDAndName {
	final String type;
	public final List<TransformationElement> transformElements; 
	public final List<InstanceGeometry> geometyInstances;

	public Node(Collada c, Element element) {
		super(element);
		type = element.getAttribute("type");
		
		transformElements = new ArrayList<>();
		XMLUtils.forEachChildElementWithName("matrix", element, el -> {
			Matrix m = new Matrix(el);
			transformElements.add(m);
		});
		
		geometyInstances = new ArrayList<>();
		XMLUtils.forEachChildElementWithName("instance_geometry", element, geomEl -> {
			InstanceGeometry g = new InstanceGeometry(c, geomEl);
			geometyInstances.add(g);
		});
	}

	
}

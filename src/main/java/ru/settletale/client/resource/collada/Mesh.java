package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Mesh {
	public final List<Source> sources;
	public final Vertices vertices;
	public final List<Polylist> polylists;
	
	public Mesh(Element meshElement) {
		sources = new ArrayList<>();
		
		XMLUtils.forEachChildElementWithName("source", meshElement, sourceElement -> {
			Source source = new Source(sourceElement);
			sources.add(source);
		});
		
		vertices = new Vertices(this, XMLUtils.getFirstChildElement("vertices", meshElement));
		polylists = new ArrayList<>();
		
		XMLUtils.forEachChildElementWithName("polylist", meshElement, elem -> {
			Polylist p = new Polylist(this, elem);
			polylists.add(p);
		});
	}
	
	public Source getSource(String id) {
		id = id.replace("#", "");
		
		for(Source s : sources) {
			if(s.id.equals(id)) 
				return s;
		}
		
		return null;
	}
}

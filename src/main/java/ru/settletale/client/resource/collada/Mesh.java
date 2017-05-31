package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class Mesh {
	public final List<Source> sources;
	
	public Mesh(Element meshElement) {
		sources = new ArrayList<>();
		
		XMLUtils.forEachChildElementWithName("source", meshElement, sourceElement -> {
			Source source = new Source(sourceElement);
			sources.add(source);
		});
	}
	
	public Source getSource(String id) {
		for(Source s : sources) {
			if(s.id.equals(id)) 
				return s;
		}
		
		return null;
	}
}

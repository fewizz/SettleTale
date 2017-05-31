package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

public class Input {
	public final String source;
	public final Semantic semantic;
	public final int offset;
	
	public Input(Element element) {
		source = element.getAttribute("source");
		semantic = Semantic.getByName(element.getAttribute("semantic"));
		
		if(element.hasAttribute("offset"))
			offset = Integer.parseInt(element.getAttribute("offset"));
		else 
			offset = -1;
	}
	
	public boolean hasOffset() {
		return offset != -1;
	}
	
	public static enum Semantic {
		POSITION,
		VERTEX,
		NORMAL;
		
		public static Semantic getByName(String name) {
			for(Semantic s : Semantic.values()) {
				if(s.name().equals(name)) {
					return s;
				}
			}
			
			return null;
		}
	}
}

package ru.settletale.client.resource.collada;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import ru.settletale.util.XMLUtils;

public class LibraryVisualScenes {
	public final List<VisualScene> visualScenesList;
	
	public LibraryVisualScenes(Element el) {
		this.visualScenesList = new ArrayList<>();
		
		XMLUtils.forEachChildElement(el, vsElement -> {
			VisualScene vs = new VisualScene(vsElement);
			visualScenesList.add(vs);
		});
	}
}

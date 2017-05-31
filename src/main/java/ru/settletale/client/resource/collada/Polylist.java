package ru.settletale.client.resource.collada;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Element;

import ru.settletale.util.StringUtils;
import ru.settletale.util.XMLUtils;

public class Polylist {
	final int count;
	final List<Input> inputs;
	final IntBuffer vCounts;
	final IntBuffer indexes;
	
	public Polylist(Element element) {
		count = Integer.parseInt(element.getAttribute("count"));
		
		inputs = new ArrayList<>();
		
		XMLUtils.forEachChildElementWithName("input", element, e -> {
			inputs.add(new Input(e));
		});
		
		vCounts = MemoryUtil.memAllocInt(count);
		indexes = MemoryUtil.memAllocInt(count * inputs.size());
		
		Element vCountElement = XMLUtils.getFirstChildElement("vcount", element);
		StringUtils.readInts(vCountElement.getTextContent(), vCounts);
		
		Element pElement = XMLUtils.getFirstChildElement("p", element);
		StringUtils.readInts(pElement.getTextContent(), indexes);
	}
}

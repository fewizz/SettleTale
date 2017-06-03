package ru.settletale.client.resource.collada;

import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Element;

import ru.settletale.util.StringUtils;
import ru.settletale.util.XMLUtils;

public class Polylist extends ColladaPrimitiveContainer {
	final int count;
	public final IntBuffer vCounts;
	public final IntBuffer indexes;
	
	public Polylist(Mesh mesh, Element element) {
		super(mesh, element);
		
		count = Integer.parseInt(element.getAttribute("count"));
		
		vCounts = MemoryUtil.memAllocInt(count);
		
		Element vCountElement = XMLUtils.getFirstChildElement("vcount", element);
		StringUtils.readInts(vCountElement.getTextContent(), vCounts);
		
		int indexesSize = 0;
		
		for(int i = 0; i < vCounts.capacity(); i++) {
			indexesSize += vCounts.get(i) * inputs.size();
		}
		
		indexes = MemoryUtil.memAllocInt(indexesSize);
		Element pElement = XMLUtils.getFirstChildElement("p", element);
		StringUtils.readInts(pElement.getTextContent(), indexes);
	}

	@Override
	public int getTotalUsedVertexCount() {
		int count = 0;
		for(int i = 0; i < vCounts.capacity(); i++) {
			count += vCounts.get(i);
		}
		
		return count;
	}
}

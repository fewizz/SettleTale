package ru.settletale.client.resource.collada;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Element;

import ru.settletale.util.StringUtils;

public class FloatArray extends ColladaArray {
	public final FloatBuffer buffer;
	
	public FloatArray(Element arrayElement) {
		super(arrayElement);
		
		int count = Integer.parseInt(arrayElement.getAttribute("count"));
		
		String text = arrayElement.getTextContent();
		
		buffer = MemoryUtil.memAllocFloat(count);
		StringUtils.readFloats(text, buffer);
	}
}

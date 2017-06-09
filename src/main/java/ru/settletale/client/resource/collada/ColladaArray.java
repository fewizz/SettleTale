package ru.settletale.client.resource.collada;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Element;

import ru.settletale.util.PrimitiveType;
import ru.settletale.util.StringUtils;

public class ColladaArray extends ObjectWithIDAndName {
	public final ByteBuffer buffer;
	public final PrimitiveType primitiveType;
	public final int count;

	public ColladaArray(Element element) {
		super(element);
		
		count = Integer.parseInt(element.getAttribute("count"));
		String text = element.getTextContent();
		
		if(element.getNodeName().equals("float_array")) {
			primitiveType = PrimitiveType.FLOAT;
			buffer = MemoryUtil.memAlloc(count * primitiveType.getSizeInBytes());
			StringUtils.readFloats(text, buffer);
		}
		else {
			throw new Error("Undefined primitive type");
		}
	}
}

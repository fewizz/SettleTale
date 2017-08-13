package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.client.render.util.GLPrimitive;
import ru.settletale.memory.MemoryBlock;
import ru.settletale.util.StringUtils;

public class ColladaArray extends ObjectWithIDAndName {
	public final MemoryBlock memoryBlock;
	public final GLPrimitive primitiveType;
	public final int count;

	public ColladaArray(Element element) {
		super(element);
		
		count = Integer.parseInt(element.getAttribute("count"));
		String text = element.getTextContent();
		
		if(element.getNodeName().equals("float_array")) {
			primitiveType = GLPrimitive.FLOAT;
			memoryBlock = new MemoryBlock().allocate(count * Float.BYTES);
			StringUtils.readFloats(text, memoryBlock);
		}
		else {
			throw new Error("Undefined primitive type");
		}
	}
}

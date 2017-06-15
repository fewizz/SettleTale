package ru.settletale.client.resource.collada;

import org.w3c.dom.Element;

import ru.settletale.memory.MemoryBlock;
import ru.settletale.util.PrimitiveType;
import ru.settletale.util.StringUtils;

public class ColladaArray extends ObjectWithIDAndName {
	public final MemoryBlock memoryBlock;
	public final PrimitiveType primitiveType;
	public final int count;

	public ColladaArray(Element element) {
		super(element);
		
		count = Integer.parseInt(element.getAttribute("count"));
		String text = element.getTextContent();
		
		if(element.getNodeName().equals("float_array")) {
			primitiveType = PrimitiveType.FLOAT;
			memoryBlock = new MemoryBlock().allocateF(count);
			StringUtils.readFloats(text, memoryBlock);
		}
		else {
			throw new Error("Undefined primitive type");
		}
	}
}

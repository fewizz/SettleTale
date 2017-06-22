package ru.settletale.client.resource.collada;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Element;

import ru.settletale.util.StringUtils;

public class Matrix extends TransformationElement {
	public final Matrix4f matrix;
	
	public Matrix(Element el) {
		super(el);
		FloatBuffer matElements = MemoryUtil.memAllocFloat(4 * 4);
		StringUtils.readFloats(el.getTextContent(), matElements);
		
		matrix = new Matrix4f(matElements);
		
		MemoryUtil.memFree(matElements);
	}
}

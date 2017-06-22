package ru.settletale.client.resource.collada;

import java.nio.FloatBuffer;

import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.w3c.dom.Element;

import ru.settletale.util.StringUtils;
import ru.settletale.util.XMLUtils;

public class Phong {
	public final Vector4f emission = new Vector4f();
	public final Vector4f ambient = new Vector4f();
	public final Vector4f diffuse = new Vector4f();
	public final Vector4f specular = new Vector4f();

	public Phong(Element el) {
		FloatBuffer buff = MemoryUtil.memAllocFloat(4);
		
		StringUtils.readFloats(XMLUtils.getFirstChildElement("emission", el).getTextContent(), buff);
		emission.set(buff);
		
		StringUtils.readFloats(XMLUtils.getFirstChildElement("ambient", el).getTextContent(), buff);
		ambient.set(buff);
		
		StringUtils.readFloats(XMLUtils.getFirstChildElement("diffuse", el).getTextContent(), buff);
		diffuse.set(buff);
		
		StringUtils.readFloats(XMLUtils.getFirstChildElement("specular", el).getTextContent(), buff);
		specular.set(buff);
		
		MemoryUtil.memFree(buff);
	}
}

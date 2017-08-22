package ru.settletale.client.render;

import ru.settletale.client.render.util.GLPrimitive;

public class Attrib {
	public final int location;
	public final GLPrimitive clientDataType;
	public final GLPrimitive serverDataType;
	public final int components;
	public final boolean normalize;
	public final int componentsBytes;
	
	private Attrib(int location, GLPrimitive clientDataType, GLPrimitive serverDataType, int components, boolean normalize) {
		this.location = location;
		this.clientDataType = clientDataType;
		this.serverDataType = serverDataType;
		this.components = components;
		this.normalize = normalize;
		this.componentsBytes = components * clientDataType.bytes;
	}
	
	public static Attrib intType(int location, GLPrimitive clientDataType, int components) {
		return new Attrib(location, clientDataType, GLPrimitive.INT, components, false);
	}
	
	public static Attrib floatType(int location, GLPrimitive clientDataType, int components, boolean normalize) {
		return new Attrib(location, clientDataType, GLPrimitive.FLOAT, components, normalize);
	}
}
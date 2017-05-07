package ru.settletale.client.render;

import org.joml.Vector3f;

public class Material {
	static final Vector3f WHITE = new Vector3f(1F);
	
	final public Vector3f colorDiffuse;
	final public Vector3f colorAmbient;
	final public Vector3f colorSpecular;
	
	public Material() {
		colorDiffuse = new Vector3f(WHITE);
		colorAmbient = new Vector3f(WHITE);
		colorSpecular = new Vector3f(WHITE);
	}
	
}

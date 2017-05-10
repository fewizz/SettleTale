package ru.settletale.client.render;

import org.joml.Vector3f;

public class Material {
	final public Vector3f colorDiffuse;
	final public Vector3f colorAmbient;
	final public Vector3f colorSpecular;
	
	public Material() {
		colorDiffuse = new Vector3f();
		colorAmbient = new Vector3f();
		colorSpecular = new Vector3f();
	}
	
	public Material(Color color) {
		colorDiffuse = color.getAsNewVector3f();
		colorAmbient = color.getAsNewVector3f();
		colorSpecular = color.getAsNewVector3f();
	}
	
}

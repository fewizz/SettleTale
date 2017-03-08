package ru.settletale.client.render;

import org.joml.Vector3f;

import ru.settletale.client.gl.Texture2D;

public class Material {
	static final Vector3f WHITE = new Vector3f(1F);
	
	public Material() {
		colorDiffuse = new Vector3f(WHITE);
	}
	
	public String name;
	public String texturePathDiffuse;
	final public Vector3f colorDiffuse;
	public Texture2D textureDiffuse;
}

package ru.settletale.client.render;

import org.joml.Vector4f;

import ru.settletale.client.gl.Texture2D;

public class Material {
	static final Vector4f WHITE = new Vector4f(1F);
	
	public Material() {
		colorDiffuse = new Vector4f(WHITE);
	}
	
	public String name;
	public String texturePathDiffuse;
	final public Vector4f colorDiffuse;
	public Texture2D textureDiffuse;
}

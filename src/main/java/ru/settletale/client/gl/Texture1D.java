package ru.settletale.client.gl;

import org.lwjgl.opengl.GL11;

public class Texture1D extends Texture {
	
	@Override
	public Texture1D gen() {
		return (Texture1D) gen(GL11.GL_TEXTURE_1D);
	}
}

package ru.settletale.client.gl;

import org.lwjgl.opengl.GL30;

public class Texture2DArray extends Texture {
	
	@Override
	public Texture gen() {
		return super.gen(GL30.GL_TEXTURE_2D_ARRAY);
	}

}

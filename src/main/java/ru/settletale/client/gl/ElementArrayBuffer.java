package ru.settletale.client.gl;

import org.lwjgl.opengl.GL15;

public class ElementArrayBuffer extends GLBuffer<ElementArrayBuffer> {

	public ElementArrayBuffer() {
		super(GL15.GL_ELEMENT_ARRAY_BUFFER);
	}

}

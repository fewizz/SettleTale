package ru.settletale.client.gl;

import org.lwjgl.opengl.GL15;

public class ElementArrayBufferObject extends BufferObject<ElementArrayBufferObject> {

	public ElementArrayBufferObject() {
		super(GL15.GL_ELEMENT_ARRAY_BUFFER);
	}

}

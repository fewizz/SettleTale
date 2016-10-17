package ru.settletale.client.opengl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Matrix4fv extends Matrix4f {
	final FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 4);;
	
	public FloatBuffer get() {
		return super.get(buffer);
	}
}

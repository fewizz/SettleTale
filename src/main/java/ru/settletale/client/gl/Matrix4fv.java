package ru.settletale.client.gl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Matrix4fv extends Matrix4f {
	public final FloatBuffer buffer;
	final FloatBuffer[] stack = new FloatBuffer[32];
	int currentIndex = 0;

	public Matrix4fv() {
		buffer = BufferUtils.createFloatBuffer(4 * 4);

		for (int i = 0; i < stack.length; i++) {
			stack[i] = BufferUtils.createFloatBuffer(4 * 4);
		}
	}

	public void push() {
		updateBackedBuffer();
		stack[currentIndex].put(buffer);
		stack[currentIndex].position(0);
		buffer.position(0);
		currentIndex++;
	}

	public void pop() {
		stack[currentIndex].clear();
		currentIndex--;
		
		buffer.put(stack[currentIndex]);
		stack[currentIndex].position(0);
		buffer.position(0);
		super.set(buffer);
	}

	public void updateBackedBuffer() {
		buffer.position(0);
		super.get(buffer);
	}

	public void rotateDeg(float ang, float x, float y, float z) {
		super.rotate((float) Math.toRadians(ang), x, y, z);
	}
	
	public void perspectiveDeg(float fovy, float aspect, float zNear, float zFar) {
		super.perspective((float) Math.toRadians(fovy), aspect, zNear, zFar);
	}
}

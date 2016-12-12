package ru.settletale.client.opengl;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

public class Matrix4fv extends Matrix4f {
	final FloatBuffer buffer;
	final FloatBuffer[] stack = new FloatBuffer[32];
	int currentIndex = -1;
	
	public Matrix4fv() {
		buffer = BufferUtils.createFloatBuffer(4 * 4);
		
		for(int i = 0; i < stack.length; i++) {
			stack[i] = BufferUtils.createFloatBuffer(4 * 4);
		}
	}
	
	public void push() {
		currentIndex++;
		
		updateBuffer();
		stack[currentIndex].put(buffer);
		
		buffer.position(0);
		stack[currentIndex].position(0);
	}
	
	public void pop() {
		buffer.put(stack[currentIndex]);
		
		buffer.position(0);
		super.set(buffer);
		
		stack[currentIndex].position(0);
		
		stack[currentIndex].clear();
		currentIndex--;
	}
	
	public void updateBuffer() {
		buffer.position(0);
		super.get(buffer);
	}
	
	public void rotateDeg(float ang, float x, float y, float z) {
		super.rotate((float) Math.toRadians(ang), x, y, z);
	}
}

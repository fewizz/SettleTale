package ru.settletale.util;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public class Matrix4fs extends Matrix4f {
	public final FloatBuffer buffer;
	final FloatBuffer[] stack = new FloatBuffer[32];
	int index = 0;

	public Matrix4fs() {
		buffer = MemoryUtil.memAllocFloat(4 * 4);

		for (int i = 0; i < stack.length; i++) {
			stack[i] = MemoryUtil.memAllocFloat(4 * 4);
		}
	}

	public void push() {
		updateBackedBuffer();
		
		stack[index].put(buffer);
		stack[index].position(0);
		buffer.position(0);
		
		index++;
	}

	public void pop() {
		stack[index].clear();
		index--;
		
		buffer.put(stack[index]);
		stack[index].position(0);
		buffer.position(0);
		super.set(buffer);
	}

	public void updateBackedBuffer() {
		buffer.position(0);
		super.get(buffer);
	}
	
	public boolean isChangedScinceLastUpdate() {
		if(buffer.get(0) != m00() || buffer.get(1) != m01() || buffer.get(2) != m02() || buffer.get(3) != m03())
			return false;
		if(buffer.get(4) != m10() || buffer.get(5) != m11() || buffer.get(6) != m12() || buffer.get(7) != m13())
			return false;
		if(buffer.get(8) != m20() || buffer.get(9) != m21() || buffer.get(10) != m22() || buffer.get(11) != m23())
			return false;
		if(buffer.get(12) != m10() || buffer.get(13) != m31() || buffer.get(14) != m32() || buffer.get(15) != m33())
			return false;
		
		return true;
	}

	public void rotateDeg(float ang, float x, float y, float z) {
		super.rotate((float) Math.toRadians(ang), x, y, z);
	}
	
	public void perspectiveDeg(float fovy, float aspect, float zNear, float zFar) {
		super.perspective((float) Math.toRadians(fovy), aspect, zNear, zFar);
	}
}

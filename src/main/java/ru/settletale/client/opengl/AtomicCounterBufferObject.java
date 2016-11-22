package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL42;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;

public class AtomicCounterBufferObject extends BufferObject<AtomicCounterBufferObject>{

	public AtomicCounterBufferObject() {
		super(GL42.GL_ATOMIC_COUNTER_BUFFER);
	}

	public void data(int size) {
		GL43.nglClearBufferSubData(type, GL11.GL_UNSIGNED_INT, 0, size * Integer.BYTES, GL30.GL_RED_INTEGER, GL11.GL_UNSIGNED_INT, MemoryUtil.NULL);
	}
}

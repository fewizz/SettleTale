package ru.settletale.client.gl;

import org.lwjgl.opengl.GL42;

public class AtomicCounterBuffer extends GLBuffer<AtomicCounterBuffer>{
	
	@Override
	public AtomicCounterBuffer gen() {
		return super.gen(GL42.GL_ATOMIC_COUNTER_BUFFER);
	}
}

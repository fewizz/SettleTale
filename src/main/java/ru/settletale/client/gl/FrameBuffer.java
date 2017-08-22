package ru.settletale.client.gl;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class FrameBuffer extends GLBindableObject<FrameBuffer> {
	public static final FrameBuffer DEFAULT = new FrameBuffer() {
		@Override
		public FrameBuffer gen() {
			throw new Error();
		}
		
		@Override
		public int getID() {
			return 0;
		}
		
		@Override
		public void delete() {
			throw new Error();
		}
	};
	
	public int target;
	
	public FrameBuffer() {
		super(FrameBuffer.class);
	}
	
	public FrameBuffer gen(int target) {
		this.target = target;
		return super.gen();
	}

	@Override
	protected void bindInternal() {
		GL30.glBindFramebuffer(target, getID());
	}

	@Override
	protected int genInternal() {
		return GL30.glGenFramebuffers();
	}

	@Override
	protected void deleteInternal() {
		GL30.glDeleteFramebuffers(getID());
	}
	
	public void texture(Texture tex, int attachment, int level) {
		GL32.glFramebufferTexture(target, attachment, tex.getID(), level);
	}

}

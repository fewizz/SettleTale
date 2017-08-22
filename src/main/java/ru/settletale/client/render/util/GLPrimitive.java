package ru.settletale.client.render.util;

import org.lwjgl.opengl.GL11;

public enum GLPrimitive {
	FLOAT(GL11.GL_FLOAT, Float.BYTES, false),
	INT(GL11.GL_INT, Integer.BYTES, true),
	BYTE(GL11.GL_BYTE, Byte.BYTES, true),
	UBYTE(GL11.GL_UNSIGNED_BYTE, Byte.BYTES, true),
	DOUBLE(GL11.GL_DOUBLE, Double.BYTES, false),
	SHORT(GL11.GL_SHORT, Short.BYTES, true),
	USHORT(GL11.GL_UNSIGNED_SHORT, Short.BYTES, true);
	
	public final boolean isIntegral;
	public final int bytes;
	public final int code;
	
	private GLPrimitive(int code, int sizeInBytes, boolean isIntegral) {
		this.code = code;
		this.bytes = sizeInBytes;
		this.isIntegral = isIntegral;
	}
	
	public static GLPrimitive getFromGLCode(int code) {
		for(GLPrimitive p : GLPrimitive.values()) {
			if(code == p.code) 
				return p;
		}
		
		throw new Error("Primitive not found");
	}
}

package ru.settletale.util;

import org.lwjgl.opengl.GL11;

public class ClientUtils {

	public static int getGLPrimitive(Primitive p) {
		switch (p) {
			case FLOAT:
				return GL11.GL_FLOAT;

			case BYTE:
				return GL11.GL_UNSIGNED_BYTE;
				
			case INT:
				return GL11.GL_INT;
			default:
				break;
		}
		
		return -1;
	}
}

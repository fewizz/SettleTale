package ru.settletale.client.gl;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import ru.settletale.util.Matrix4fs;
import ru.settletale.util.PrimitiveType;

public class GL {
	private static Texture<?>[] activeTextures;
	private static int activeTextureUnitIndex = 0;
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static String vendor;
	public static int maxTextureUnitsAmount;
	public static final Matrix4fs PROJ_MATRIX = new Matrix4fs();
	public static final Matrix4fs VIEW_MATRIX = new Matrix4fs();

	public static void init() {
		vendor = glGetString(GL_VENDOR);
		versionMajor = getInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = getInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;
		maxTextureUnitsAmount = getInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
		activeTextures = new Texture<?>[maxTextureUnitsAmount];
		for(int i = 0; i < activeTextures.length; i++) {
			activeTextures[i] = Texture.DEFAULT;
		}
		
		System.out.println("Vendor: " + GL.vendor);
	}

	public static void bindBufferBase(GLBuffer<?> buffer, int index) {
		GL30.glBindBufferBase(buffer.type, index, buffer.id);
	}

	public static void bindTextureUnit(int index, Texture<?> texture) {
		if (activeTextureUnitIndex != index) {
			setActiveTextureUnitIndex(index);
		}
		setActiveTextureUnitTexture(texture);
	}

	static void onTextureDeleted(Texture<?> texture) {
		for (int i = 0; i < activeTextures.length; i++) {
			if (activeTextures[i] == texture) {
				activeTextures[i] = Texture.DEFAULT;
			}
		}
	}

	public static void setActiveTextureUnitIndex(int index) {
		activeTextureUnitIndex = index;
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + activeTextureUnitIndex);
	}

	public static boolean setActiveTextureUnitTexture(Texture<?> tex) {
		if (activeTextures[activeTextureUnitIndex] != null && activeTextures[activeTextureUnitIndex].id == tex.id) {
			return false;
		}

		activeTextures[activeTextureUnitIndex] = tex;
		tex.bindWithForce();
		return true;
	}

	public static int getInteger(int pname) {
		return glGetInteger(pname);
	}

	public static String getErrorNameFromHex(int hex) {
		switch (hex) {
			case GL_INVALID_ENUM:
				return "GL_INVALID_ENUM";
			case GL_INVALID_VALUE:
				return "GL_INVALID_VALUE";
			case GL_INVALID_OPERATION:
				return "GL_INVALID_OPERATION";
			case GL_STACK_OVERFLOW:
				return "GL_STACK_OVERFLOW";
			case GL_STACK_UNDERFLOW:
				return "GL_STACK_UNDERFLOW";
			case GL_OUT_OF_MEMORY:
				return "GL_OUT_OF_MEMORY";
			case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
				return "GL_INVALID_FRAMEBUFFER_OPERATION";
			case GL45.GL_CONTEXT_LOST:
				return "GL_CONTEXT_LOST";
			default:
				return null;
		}
	}

	public static int getGLPrimitiveType(PrimitiveType p) {
		switch (p) {
			case FLOAT:
				return GL_FLOAT;
			case BYTE:
				return GL_BYTE;
			case UBYTE:
				return GL_UNSIGNED_BYTE;
			case INT:
				return GL_INT;
			default:
				throw new Error("Undefined type");
		}
	}
}

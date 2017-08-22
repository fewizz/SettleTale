package ru.settletale.client.gl;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GL {
	private static Texture[] activeTextures;
	private static int activeTextureUnitIndex = 0;
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static String vendor;

	public static void init() {
		org.lwjgl.opengl.GL.createCapabilities();
		
		vendor = glGetString(GL_VENDOR);
		versionMajor = getInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = getInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;
		int maxTextureUnitsAmount = getInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS);
		activeTextures = new Texture[maxTextureUnitsAmount];
		
		for(int i = 0; i < activeTextures.length; i++) {
			activeTextures[i] = Texture.DEFAULT;
		}
		
		System.out.println("Vendor: " + GL.vendor);
	}

	public static void bindBufferBase(GLBuffer<?> buffer, int index) {
		GL30.glBindBufferBase(buffer.type, index, buffer.getID());
	}

	public static void bindTextureUnit(int index, Texture texture) {
		if (activeTextureUnitIndex != index) {
			setActiveTextureUnitIndex(index);
		}
		setActiveTextureUnitTexture(texture);
	}

	static void onTextureDeleted(Texture texture) {
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

	public static boolean setActiveTextureUnitTexture(Texture tex) {
		if (activeTextures[activeTextureUnitIndex] == tex) {
			return false;
		}

		activeTextures[activeTextureUnitIndex] = tex;
		tex.bindWithForce();
		return true;
	}

	public static int getInteger(int pname) {
		return glGetInteger(pname);
	}
}

package ru.settletale.client.opengl;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import ru.settletale.client.opengl.BufferObject.Usage;

public class GL {
	public static final boolean debug = true;
	public static final boolean debugOnlyFails = true;
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static UniformBufferObject uboMatricies;
	public static UniformBufferObject uboDisplaySize;
	public static Matrix4fv projMatrix;
	public static Matrix4fv viewMatrix;
	public static FloatBuffer unifromMatriciesBuffer;
	public static FloatBuffer uniformDisplaySizeBuffer;

	public static void init() {
		debug("Init start");
		
		projMatrix = new Matrix4fv();
		viewMatrix = new Matrix4fv();
		unifromMatriciesBuffer = BufferUtils.createFloatBuffer(32);
		uniformDisplaySizeBuffer = BufferUtils.createFloatBuffer(2);
		
		uboMatricies = new UniformBufferObject().gen();
		uboMatricies.data(unifromMatriciesBuffer, Usage.DYNAMIC_DRAW);
		
		uboDisplaySize = new UniformBufferObject().gen();
		uboDisplaySize.data(uniformDisplaySizeBuffer, Usage.STATIC_DRAW);

		versionMajor = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;
		
		debug("Init end");
	}
	
	public static void updateTransformUniformBlock() {
		debug("UpdateTransformUniformBlock start");
		
		unifromMatriciesBuffer.clear();
		projMatrix.updateBuffer();
		viewMatrix.updateBuffer();
		
		for (int i = 0; i < 16; i++) {
			unifromMatriciesBuffer.put(i, projMatrix.buffer.get(i));
		}
		if (viewMatrix.buffer != null) {
			for (int i = 0; i < 16; i++) {
				unifromMatriciesBuffer.put(i + 16, viewMatrix.buffer.get(i));
			}
		}
		uboMatricies.subdata(unifromMatriciesBuffer, 0);
		bindBufferBase(uboMatricies, 0);
		
		debug("UpdateTransformUniformBlock end");
	}
	
	public static void updateDisplaySizeUniformBlock() {
		debug("UpdateDisplaySizeUniformBlock start");
		
		uboDisplaySize.subdata(uniformDisplaySizeBuffer, 0);
		bindBufferBase(uboDisplaySize, 1);
		
		debug("UpdateDisplaySizeUniformBlock end");
	}
	
	public static void bindBufferBase(BufferObject<?> buffer, int index) {
		GL30.glBindBufferBase(buffer.type, index, buffer.id);
	}
	
	public static void bindDefaultVAO() {
		GL30.glBindVertexArray(0);
	}
	
	public static void debug(String s) {
		if(debug) {
			int error = GL11.glGetError();
			if(debugOnlyFails && error == 0) {
				return;
			}
			System.out.println("OpenGL: " + error + " " + s);
		}
	}
}

package ru.settletale.client.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import ru.settletale.client.Display;
import ru.settletale.client.opengl.Shader.Type;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;

public class GL {
	public static final boolean debug = false;
	public static final boolean debugOnlyFails = false;
	public static int version;
	public static int versionMajor;
	public static int versionMinor;
	public static UniformBufferObject uboMatricies;
	public static UniformBufferObject uboDisplaySize;
	public static Matrix4fv projMatrix;
	public static Matrix4fv viewMatrix;

	// For default drawing
	static final int POSITION = 0;
	static final int COLOR = 1;
	static PrimitiveArray pa;
	static ShaderProgram program;
	static VertexArrayObject vao;
	static VertexBufferObject positionBuffer;
	static VertexBufferObject colorBuffer;
	static int drawingMode;
	static int prevVertexCount = 100; // Not true, but...)))
	static byte r = (byte) 0xFF;
	static byte g = (byte) 0xFF;
	static byte b = (byte) 0xFF;
	static byte a = (byte) 0xFF;

	public static void init() {
		debug("Init start");
		
		projMatrix = new Matrix4fv();
		viewMatrix = new Matrix4fv();
		
		try(MemoryStack ms = MemoryStack.stackPush()) {
			uboMatricies = new UniformBufferObject().gen().buffer(ms.callocFloat(32)).loadData();
			uboDisplaySize = new UniformBufferObject().gen().buffer(ms.callocFloat(2)).loadData();
		}

		versionMajor = GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
		versionMinor = GL11.glGetInteger(GL30.GL_MINOR_VERSION);
		version = versionMajor * 10 + versionMinor;
		
		pa = new PrimitiveArray(Storage.FLOAT_3, Storage.BYTE_4);
		program = new ShaderProgram().gen();
		program.attachShader(new Shader(Type.VERTEX, "shaders/default_vs.shader").gen().compile());
		program.attachShader(new Shader(Type.FRAGMENT, "shaders/default_fs.shader").gen().compile());
		program.link();
		
		vao = new VertexArrayObject().gen();
		positionBuffer = new VertexBufferObject().gen();
		colorBuffer = new VertexBufferObject().gen();
		
		debug("Init end");
	}

	public static void updateTransformUniformBlock() {
		debug("UpdateTransformUniformBlock start");

		projMatrix.updateBuffer();
		viewMatrix.updateBuffer();
		uboMatricies.buffer(projMatrix.buffer).offset(0).loadSubData();
		uboMatricies.buffer(viewMatrix.buffer).offset(16 * Float.BYTES).loadSubData();

		bindBufferBase(uboMatricies, 0);

		debug("UpdateTransformUniformBlock end");
	}

	public static void updateDisplaySizeUniformBlock() {
		debug("UpdateDisplaySizeUniformBlock start");

		try (MemoryStack ms = MemoryStack.stackPush()) {
			uboDisplaySize.buffer(ms.floats(Display.width, Display.height)).loadSubData();
			bindBufferBase(uboDisplaySize, 1);
		}

		debug("UpdateDisplaySizeUniformBlock end");
	}

	public static void bindBufferBase(BufferObject<?> buffer, int index) {
		GL30.glBindBufferBase(buffer.type, index, buffer.id);
	}

	public static void bindDefaultVAO() {
		GL30.glBindVertexArray(0);
	}

	public static void begin(int mode) {
		drawingMode = mode;
		pa.clear();
	}

	public static void color(float r, float g, float b, float a) {
		GL.r = (byte) (r * 255);
		GL.g = (byte) (g * 255);
		GL.b = (byte) (b * 255);
		GL.a = (byte) (a * 255);
	}

	public static void vertex(float x, float y, float z) {
		pa.data(POSITION, x, y, z);
		pa.data(COLOR, r, g, b, a);
		pa.endVertex();
	}

	public static void debug(String s) {
		if (debug) {
			int error = GL11.glGetError();
			if (debugOnlyFails && error == 0) {
				return;
			}
			System.out.println("OpenGL: " + error + " " + s);
		}
	}
}

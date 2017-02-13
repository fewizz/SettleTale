package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.TextureAbstract;
import ru.settletale.client.gl.VertexArrayObject;
import ru.settletale.client.gl.VertexBufferObject;
import ru.settletale.client.gl.BufferObject.Usage;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;

public class Drawer {
	static final int POSITION = 0;
	static final int COLOR = 1;
	static final int UV = 2;
	static final PrimitiveArray PRIMITIVE_ARRAY = new PrimitiveArray(Storage.FLOAT_3, Storage.BYTE_4, Storage.FLOAT_2);
	static ShaderProgram program;
	static ShaderProgram programTex;
	static VertexArrayObject vao;
	static VertexBufferObject positionBuffer;
	static VertexBufferObject colorBuffer;
	static VertexBufferObject uvBuffer;
	static TextureAbstract<?> texture;
	static int drawingMode;
	static int vertexCount;
	static boolean useTexture = false;
	static float u;
	static float v;
	static byte r = (byte) 0xFF;
	static byte g = (byte) 0xFF;
	static byte b = (byte) 0xFF;
	static byte a = (byte) 0xFF;

	public static void init() {
		program = new ShaderProgram().gen();
		program.attachShader(ShaderLoader.SHADERS.get("shaders/default.vs"));
		program.attachShader(ShaderLoader.SHADERS.get("shaders/default.fs"));
		program.link();
		programTex = new ShaderProgram().gen();
		programTex.attachShader(ShaderLoader.SHADERS.get("shaders/default_tex1.vs"));
		programTex.attachShader(ShaderLoader.SHADERS.get("shaders/default_tex1.fs"));
		programTex.link();

		vao = new VertexArrayObject().gen();

		positionBuffer = new VertexBufferObject().gen().usage(Usage.DYNAMIC_DRAW);
		colorBuffer = new VertexBufferObject().gen().usage(Usage.DYNAMIC_DRAW);
		uvBuffer = new VertexBufferObject().gen().usage(Usage.DYNAMIC_DRAW);
	}

	public static void begin(int mode) {
		drawingMode = mode;
		vertexCount = 0;
		useTexture = false;
		texture = null;
		PRIMITIVE_ARRAY.clear();
	}

	public static void draw() {
		draw(useTexture ? programTex : program);
	}
	public static void draw(ShaderProgram program) {
		GL.debug("Drawer start", true);

		positionBuffer.buffer(PRIMITIVE_ARRAY.getBuffer(POSITION)).loadData();
		colorBuffer.buffer(PRIMITIVE_ARRAY.getBuffer(COLOR)).loadData();

		vao.vertexAttribPointer(positionBuffer, 0, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);

		vao.vertexAttribPointer(colorBuffer, 1, 4, GL11.GL_UNSIGNED_BYTE, true, 0);
		vao.enableVertexAttribArray(1);

		GL.debug("Drawer pre vao bind");

		if (useTexture) {
			uvBuffer.buffer(PRIMITIVE_ARRAY.getBuffer(UV)).loadData();
			vao.vertexAttribPointer(uvBuffer, 2, 2, GL11.GL_FLOAT, false, 0);
			vao.enableVertexAttribArray(2);
			GL.activeTexture(0, texture);
			program.bind();
		}
		else {
			program.bind();
		}
		GL.debug("After program bind");
		vao.bind();
		GL.debug("After vao bind");
		GL11.glDrawArrays(drawingMode, 0, vertexCount);
		GL.debug("Draw drawArrays");
	}

	public static void color(float r, float g, float b, float a) {
		Drawer.r = (byte) (r * 255);
		Drawer.g = (byte) (g * 255);
		Drawer.b = (byte) (b * 255);
		Drawer.a = (byte) (a * 255);
	}

	public static void uv(float u, float v) {
		Drawer.u = u;
		Drawer.v = v;
	}

	public static void vertex(float x, float y, float z) {
		PRIMITIVE_ARRAY.data(POSITION, x, y, z);
		PRIMITIVE_ARRAY.data(COLOR, r, g, b, a);
		if (useTexture)
			PRIMITIVE_ARRAY.data(UV, u, v);
		PRIMITIVE_ARRAY.endVertex();
		vertexCount++;
	}

	public static void texture(TextureAbstract<?> texture) {
		Drawer.texture = texture;
		useTexture = true;
	}
}

package ru.settletale.client.render;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.TextureAbstract;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.vertex.PrimitiveArray.StorageInfo;

public class Drawer {
	static RenderLayer layer;
	static final int POSITION = 0;
	static final int COLOR = 1;
	static final int UV = 2;
	static ShaderProgram program;
	static ShaderProgram programTex;
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
		layer = new RenderLayer(StorageInfo.FLOAT_3, StorageInfo.BYTE_4, StorageInfo.FLOAT_2);
		layer.enableNormalisation(COLOR);
		
		program = new ShaderProgram().gen();
		program.attachShader(ShaderLoader.SHADERS.get("shaders/default.vs"));
		program.attachShader(ShaderLoader.SHADERS.get("shaders/default.fs"));
		program.link();
		programTex = new ShaderProgram().gen();
		programTex.attachShader(ShaderLoader.SHADERS.get("shaders/default_tex1.vs"));
		programTex.attachShader(ShaderLoader.SHADERS.get("shaders/default_tex1.fs"));
		programTex.link();
	}

	public static void begin(int mode) {
		drawingMode = mode;
		vertexCount = 0;
		useTexture = false;
		texture = null;
		layer.clear();
	}

	public static void draw() {
		draw(useTexture ? programTex : program);
	}
	public static void draw(ShaderProgram program) {
		GL.debug("Drawer start", true);

		layer.compile(true);

		GL.debug("Drawer pre vao bind");

		if (useTexture) {
			GL.activeTexture(0, texture);
		}
		layer.setShaderProgram(program);
		layer.render(drawingMode);
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
		layer.data(POSITION, x, y, z);
		layer.data(COLOR, r, g, b, a);
		if (useTexture)
			layer.data(UV, u, v);
		layer.endVertex();
	}

	public static void texture(TextureAbstract<?> texture) {
		Drawer.texture = texture;
		useTexture = true;
	}
}

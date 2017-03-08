package ru.settletale.client.render;

import org.joml.Vector2f;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.TextureAbstract;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.vertex.VertexArray.AttributeType;

public class Drawer {
	static final RenderLayer LAYER = new RenderLayer(AttributeType.FLOAT_3, AttributeType.BYTE_4_NORMALISED, AttributeType.FLOAT_2);;
	static final int POSITION_POS = 0;
	static final int COLOR_POS = 1;
	static final int UV_POS = 2;
	static final ShaderProgram PROGRAM = new ShaderProgram();
	static final ShaderProgram PROGRAM_TEX = new ShaderProgram();
	static TextureAbstract<?> texture;
	static int drawingMode;
	static int vertexCount;
	static boolean useTexture = false;
	static final Vector2f UV = new Vector2f();
	static final Color COLOR = new Color(1F, 1F, 1F, 1F);
	static float scale = 1F;

	public static void init() {
		PROGRAM.gen();
		PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/default.vs"));
		PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/default.fs"));
		PROGRAM.link();
		
		PROGRAM_TEX.gen();
		PROGRAM_TEX.attachShader(ShaderLoader.SHADERS.get("shaders/default_tex1.vs"));
		PROGRAM_TEX.attachShader(ShaderLoader.SHADERS.get("shaders/default_tex1.fs"));
		PROGRAM_TEX.link();
	}

	public static void begin(int mode) {
		drawingMode = mode;
		vertexCount = 0;
		useTexture = false;
		texture = null;

		if (LAYER.hasVertexArray()) {
			LAYER.getVertexArray().clear();
		}
	}

	public static void draw() {
		draw(useTexture ? PROGRAM_TEX : PROGRAM);
	}

	public static void draw(ShaderProgram program) {
		GL.debug("Drawer start", true);

		LAYER.compile(true);

		GL.debug("Drawer pre vao bind");

		if (useTexture) {
			GL.activeTexture(0, texture);
		}
		LAYER.setShaderProgram(program);
		LAYER.render(drawingMode);
		GL.debug("Draw drawArrays");
	}

	public static void color(float r, float g, float b, float a) {
		COLOR.set(r, g, b, a);
	}
	
	public static void color(Color c) {
		COLOR.set(c);
	}
	
	public static void scale(float scale) {
		Drawer.scale = scale;
	}

	public static void uv(float u, float v) {
		Drawer.UV.set(u, v);
	}

	public static void vertex(float x, float y) {
		vertex(x, y, 0);
	}
	
	public static void vertex(float x, float y, float z) {
		LAYER.getVertexArray().data(POSITION_POS, x * scale, y * scale, z * scale);
		LAYER.getVertexArray().data(COLOR_POS, COLOR.r(), COLOR.g(), COLOR.b(), COLOR.a());
		if (useTexture)
			LAYER.getVertexArray().data(UV_POS, UV.x, UV.y);
		LAYER.getVertexArray().endVertex();
	}

	public static void texture(TextureAbstract<?> texture) {
		Drawer.texture = texture;
		useTexture = true;
	}
}

package ru.settletale.client.render;

import org.joml.Vector2f;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.render.RenderLayerTextured.TextureUniformType;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.vertex.AttributeType;

public class Drawer {
	public static RenderLayerTextured layer;
	static final int POSITION_ID = 0;
	static final int COLOR_ID = 1;
	static final int UV_ID = 2;
	static final int TEX_ID = 3;
	static final ShaderProgram PROGRAM = new ShaderProgram();
	static final ShaderProgram PROGRAM_TEX = new ShaderProgram();
	static final ShaderProgram PROGRAM_MULTITEX = new ShaderProgram();
	static int drawingMode;
	static int vertexCount;
	static final Vector2f UV = new Vector2f();
	static final Color COLOR = new Color(1F, 1F, 1F, 1F);
	static float scale = 1F;

	public static void init() {
		PROGRAM.gen();
		PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/drawer.vs"));
		PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/drawer.fs"));
		PROGRAM.link();
		
		PROGRAM_TEX.gen();
		PROGRAM_TEX.attachShader(ShaderLoader.SHADERS.get("shaders/drawer_tex.vs"));
		PROGRAM_TEX.attachShader(ShaderLoader.SHADERS.get("shaders/drawer_tex.fs"));
		PROGRAM_TEX.link();
		
		PROGRAM_MULTITEX.gen();
		PROGRAM_MULTITEX.attachShader(ShaderLoader.SHADERS.get("shaders/drawer_multitex.vs"));
		PROGRAM_MULTITEX.attachShader(ShaderLoader.SHADERS.get("shaders/drawer_multitex.fs"));
		PROGRAM_MULTITEX.link();
		
		layer = new RenderLayerTextured(AttributeType.FLOAT_3, AttributeType.BYTE_4_NORMALISED, AttributeType.FLOAT_2, AttributeType.INT_1);
		layer.setTextureIDAttributeIndex(TEX_ID);
		layer.setTextureUniformLocation(0);
	}

	public static void begin(int mode) {
		drawingMode = mode;
		vertexCount = 0;

		layer.clearVertexAttributeArrayIdExists();
		layer.clearTextures();
	}

	public static void draw() {
		layer.setUniformType(layer.getUsedTextureCount() == 0 ? TextureUniformType.NONE : layer.getUsedTextureCount() == 1 ? TextureUniformType.VARIABLE : TextureUniformType.ARRAY);
		draw(layer.getUsedTextureCount() == 0 ? PROGRAM : layer.getUsedTextureCount() == 1 ? PROGRAM_TEX : PROGRAM_MULTITEX);
	}

	public static void draw(ShaderProgram program) {
		GL.debug("Drawer start", true);
		
		layer.compile(true);

		GL.debug("Drawer pre vao bind");

		layer.setShaderProgram(program);
		layer.render(drawingMode);
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
		layer.getVertexAttributeArray().data(POSITION_ID, x * scale, y * scale, z * scale);
		layer.getVertexAttributeArray().data(COLOR_ID, COLOR.r(), COLOR.g(), COLOR.b(), COLOR.a());
		layer.getVertexAttributeArray().data(UV_ID, UV.x, UV.y);
		layer.getVertexAttributeArray().endVertex();
	}
}

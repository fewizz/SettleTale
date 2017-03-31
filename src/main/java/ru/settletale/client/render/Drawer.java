package ru.settletale.client.render;

import org.joml.Vector2f;
import org.joml.Vector3f;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.vertex.VertexAttribType;

public class Drawer {
	public static final RenderLayerTextured LAYER = new RenderLayerTextured(VertexAttribType.FLOAT_3, VertexAttribType.UBYTE_4_FLOAT_4_NORMALISED, VertexAttribType.FLOAT_2, VertexAttribType.UBYTE_1_INT_1);;
	static final int POSITION_ID = 0;
	static final int COLOR_ID = 1;
	static final int UV_ID = 2;
	static final int TEX_ID = 3;
	static final ShaderProgram PROGRAM = new ShaderProgram();
	static final ShaderProgram PROGRAM_TEX = new ShaderProgram();
	static final ShaderProgram PROGRAM_MULTITEX = new ShaderProgram();
	static final Vector2f UV = new Vector2f();
	static final Color COLOR = new Color(1F, 1F, 1F, 1F);
	static final Vector3f SCALE = new Vector3f(1F);
	static int drawingMode;
	static int vertexCount;

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
		
		LAYER.setTextureAttribIndex(TEX_ID);
		LAYER.setTextureUniformLocation(0);
		LAYER.setAllowSubData(true);
	}

	public static void begin(int mode) {
		drawingMode = mode;
		vertexCount = 0;

		LAYER.clearVertexAttribArrayIfExists();
		LAYER.clearTextures();
	}

	public static void draw() {
		UniformType type;
		ShaderProgram program;
		
		if(LAYER.getUsedTextureCount() == 0) {
			program = PROGRAM;
			type = null;
		}
		else if(LAYER.getUsedTextureCount() == 1) {
			type = UniformType.INT;
			program = PROGRAM_TEX;
		}
		else {
			type = UniformType.INT_ARRAY;
			program = PROGRAM_MULTITEX;
		}
		
		draw(program, type);
	}

	public static void draw(ShaderProgram program, UniformType type) {
		GL.debug("Drawer start", true);
		
		LAYER.setUniformType(type);
		LAYER.compile();

		GL.debug("Drawer pre vao bind");

		LAYER.setShaderProgram(program);
		LAYER.render(drawingMode);
		GL.debug("Draw drawArrays");
	}
	
	public static void texture(Texture2D tex) {
		LAYER.setTexture(tex);
	}

	public static void color(float r, float g, float b, float a) {
		COLOR.set(r, g, b, a);
	}
	
	public static void color(Color c) {
		COLOR.set(c);
	}
	
	public static void scale(float scale) {
		SCALE.set(scale);
	}

	public static void uv(float u, float v) {
		UV.set(u, v);
	}

	public static void vertex(float x, float y) {
		vertex(x, y, 0);
	}
	
	public static void vertex(float x, float y, float z) {
		LAYER.getVertexArrayDataBaker().putFloat(POSITION_ID, x * SCALE.x, y * SCALE.y, z * SCALE.z);
		LAYER.getVertexArrayDataBaker().putByte(COLOR_ID, COLOR.r(), COLOR.g(), COLOR.b(), COLOR.a());
		LAYER.getVertexArrayDataBaker().putFloat(UV_ID, UV.x, UV.y);
		LAYER.getVertexArrayDataBaker().endVertex();
	}
}

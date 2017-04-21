package ru.settletale.client.render;

import org.joml.Vector2f;
import org.joml.Vector3f;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.resource.ShaderLoader;
import ru.settletale.client.vertex.VertexAttribType;

public class Drawer {
	public static final RenderLayer LAYER = new RenderLayer(VertexAttribType.FLOAT_3, VertexAttribType.UBYTE_4_FLOAT_4_NORMALISED, VertexAttribType.FLOAT_2, VertexAttribType.UBYTE_1_INT_1);
	public static final TextureUnitBinder TEXTURE_UNIT_BINDER = new TextureUnitBinder();
	public static final Vector2f UV = new Vector2f();
	public static final Color COLOR = new Color(Color.WHITE);
	public static final Vector3f SCALE = new Vector3f(1F);
	
	static final ShaderProgram PROGRAM = new ShaderProgram();
	static final ShaderProgram PROGRAM_TEX = new ShaderProgram();
	static final ShaderProgram PROGRAM_MULTITEX = new ShaderProgram();
	
	private static int drawingMode;
	
	static final int POSITION_ID = 0;
	static final int COLOR_ID = 1;
	static final int UV_ID = 2;
	static final int TEX_ID = 3;

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
		
		LAYER.setAllowSubDataWhenPossible(true);
	}

	public static void begin(int mode) {
		drawingMode = mode;

		LAYER.clearVertexArrayDataBakerIfExists();
		TEXTURE_UNIT_BINDER.clear();
	}

	public static void draw() {
		ShaderProgram program;
		int textureCount = TEXTURE_UNIT_BINDER.getUsedTextureUnitCount();
		
		if(textureCount == 0) {
			program = PROGRAM;
		}
		else if(textureCount == 1) {
			program = PROGRAM_TEX;
		}
		else {
			program = PROGRAM_MULTITEX;
		}
		
		draw(program);
	}

	public static void draw(ShaderProgram program) {
		GL.debug("Drawer start", true);
		
		LAYER.compile();

		GL.debug("Drawer pre vao bind");
		TEXTURE_UNIT_BINDER.updateUniforms(program);
		TEXTURE_UNIT_BINDER.bindTextures();
		LAYER.setShaderProgram(program);
		LAYER.render(drawingMode);
		GL.debug("Draw drawArrays");
	}
	
	public static void texture(Texture2D tex) {
		TEXTURE_UNIT_BINDER.setCurrentUniformLocation(0);
		byte arrayIndex = (byte) TEXTURE_UNIT_BINDER.use(tex);
		LAYER.getVertexArrayDataBaker().putByte(TEX_ID, arrayIndex);
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

package ru.settletale.client.render;

import org.joml.Vector2f;
import org.joml.Vector3f;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Shader;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.gl.Shader.ShaderType;
import ru.settletale.client.render.vertex.VertexArrayDataBaker;
import ru.settletale.client.render.vertex.VertexArrayRenderer;
import ru.settletale.client.render.vertex.VertexAttribType;
import ru.settletale.client.render.vertex.VertexArrayRenderer.GLDrawFunc;
import ru.settletale.client.resource.loader.ShaderSourceLoader;
import ru.settletale.memory.MemoryBlock;
import ru.settletale.util.AdvancedArrayList;
import ru.settletale.util.AdvancedList;

public class Drawer {
	public static final VertexArrayRenderer VERTEX_ARRAY_RENDERER = new VertexArrayRenderer();
	public static final VertexArrayDataBaker VERTEX_ARRAY_DATA_BAKER = new VertexArrayDataBaker(1024,  true, VertexAttribType.FLOAT_3, VertexAttribType.UBYTE_4_FLOAT_4_NORMALISED, VertexAttribType.FLOAT_2, VertexAttribType.UBYTE_1_INT_1);
	
	static final AdvancedList<Texture2D> TEXTURES = new AdvancedArrayList<>();
	public static final Vector2f UV = new Vector2f();
	public static final Color COLOR = new Color(Color.WHITE);
	public static final Vector3f SCALE = new Vector3f(1F);

	static final ShaderProgram PROGRAM = new ShaderProgram();
	static final ShaderProgram PROGRAM_TEX = new ShaderProgram();
	static final ShaderProgram PROGRAM_MULTITEX = new ShaderProgram();

	static final int POSITION_ID = 0;
	static final int COLOR_ID = 1;
	static final int UV_ID = 2;
	static final int TEX_ID = 3;

	static final Runnable TEX_BINDER_MUTLI = () -> {
		TEXTURES.forEachIndexed((int index, Texture2D tex) -> {
			GL.bindTextureUnit(index, tex);
		});

		MemoryBlock mb = new MemoryBlock().allocate(TEXTURES.size() * Integer.BYTES);

		TEXTURES.forEachIndexed((int index, Texture2D tex) -> {
			mb.putIntI(index, index);
		});
		VERTEX_ARRAY_RENDERER.program.setUniformIntArray(0, mb);

		mb.free();
	};

	static final Runnable TEX_BINDER = () -> {
		GL.bindTextureUnit(0, TEXTURES.get(0));
	};
	
	private static int drawingMode;

	public static void init() {
		PROGRAM.gen();
		PROGRAM.attachShader(new Shader().gen(ShaderType.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/drawer.vs")));
		PROGRAM.attachShader(new Shader().gen(ShaderType.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/drawer.fs")));
		PROGRAM.link();

		PROGRAM_TEX.gen();
		PROGRAM_TEX.attachShader(new Shader().gen(ShaderType.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/drawer_tex.vs")));
		PROGRAM_TEX.attachShader(new Shader().gen(ShaderType.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/drawer_tex.fs")));
		PROGRAM_TEX.link();

		PROGRAM_MULTITEX.gen();
		PROGRAM_MULTITEX.attachShader(new Shader().gen(ShaderType.VERTEX).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/drawer_multitex.vs")));
		PROGRAM_MULTITEX.attachShader(new Shader().gen(ShaderType.FRAGMENT).source(ShaderSourceLoader.SHADER_SOURCES.get("shaders/drawer_multitex.fs")));
		PROGRAM_MULTITEX.link();

		//LAYER.setAllowSubDataWhenPossible(true);
	}

	public static void begin(int mode) {
		drawingMode = mode;

		VERTEX_ARRAY_DATA_BAKER.clear();
		TEXTURES.clear();
	}

	public static void draw() {
		int textureCount = TEXTURES.size();

		if (textureCount == 0) {
			draw(PROGRAM, null);
		} else if (textureCount == 1) {
			draw(PROGRAM_TEX, TEX_BINDER);
		} else {
			draw(PROGRAM_MULTITEX, TEX_BINDER_MUTLI);
		}
	}

	public static void draw(ShaderProgram program, Runnable runBindTextures) {
		Renderer.debugGL("Drawer start", true);

		VERTEX_ARRAY_RENDERER.compile(VERTEX_ARRAY_DATA_BAKER);

		Renderer.debugGL("Drawer pre vao bind");

		VERTEX_ARRAY_RENDERER.setShaderProgram(program);
		if(runBindTextures != null) runBindTextures.run();
		
		VERTEX_ARRAY_RENDERER.render(GLDrawFunc.DRAW_ARRAYS, drawingMode);
		Renderer.debugGL("Draw drawArrays");
	}

	public static void texture(Texture2D tex) {
		TEXTURES.addIfAbsent(tex);
		byte arrayIndex = (byte) TEXTURES.indexOf(tex);
		VERTEX_ARRAY_DATA_BAKER.putByte(TEX_ID, arrayIndex);
	}

	public static void vertex(float x, float y) {
		vertex(x, y, 0);
	}

	public static void vertex(float x, float y, float z) {
		VERTEX_ARRAY_DATA_BAKER.putFloats(POSITION_ID, x * SCALE.x, y * SCALE.y, z * SCALE.z);
		VERTEX_ARRAY_DATA_BAKER.putBytes(COLOR_ID, COLOR.r(), COLOR.g(), COLOR.b(), COLOR.a());
		VERTEX_ARRAY_DATA_BAKER.putFloats(UV_ID, UV);
		VERTEX_ARRAY_DATA_BAKER.endVertex();
	}
}

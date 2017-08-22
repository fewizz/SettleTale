package ru.settletale.client.render;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.gl.VertexArray;
import ru.settletale.client.render.util.GLPrimitive;
import ru.settletale.client.render.util.GLUtils;
import ru.settletale.memory.MemoryBlock;

public class Drawer {
	static final VertexAttribArray ATTRIB_POS = new VertexAttribArray(Attrib.floatType(0, GLPrimitive.FLOAT, 3, false), 1024);
	static final VertexAttribArray ATTRIB_COLOR = new VertexAttribArray(Attrib.floatType(1, GLPrimitive.UBYTE, 4, true), 1024);
	static final VertexAttribArray ATTRIB_UV = new VertexAttribArray(Attrib.floatType(2, GLPrimitive.FLOAT, 2, false), 1024);
	static final VertexAttribArray ATTRIB_TEX = new VertexAttribArray(Attrib.intType(3, GLPrimitive.USHORT, 1), 1024);
	static final VertexArray VAO = new VertexArray();
	
	static Texture2D[] textures; 
	public static final Vector2f UV = new Vector2f();
	public static final Color COLOR = new Color(Color.WHITE);
	public static final Vector3f SCALE = new Vector3f(1F);

	static final ShaderProgram PROGRAM = new ShaderProgram();
	static final ShaderProgram PROGRAM_TEX = new ShaderProgram();
	static final ShaderProgram PROGRAM_MULTITEX = new ShaderProgram();
	
	private static int texturesCount = 0;
	private static int drawingMode;
	private static int vertex;
	
	public static final ITexBinder TEX_BINDER_MULTI = program -> {
		MemoryBlock mb = new MemoryBlock().allocateI(texturesCount);
		
		for(int t = 0; t < texturesCount; t++) {
			GL.bindTextureUnit(t, textures[t]);
			mb.putIntI(t, t);
		}
		
		program.setUniformIntArray(0, mb);

		mb.free();
	};

	public static void init() {
		textures = new Texture2D[GL.getInteger(GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS)];
		GLUtils.linkShadersToProgram(PROGRAM, "shaders/drawer.vs", "shaders/drawer.fs");
		GLUtils.linkShadersToProgram(PROGRAM_TEX, "shaders/drawer_tex.vs", "shaders/drawer_tex.fs");
		GLUtils.linkShadersToProgram(PROGRAM_MULTITEX, "shaders/drawer_multitex.vs", "shaders/drawer_multitex.fs");
		
		VAO.gen().bind();
		
		VAO.vertexAttribPointer(ATTRIB_POS);
		VAO.vertexAttribPointer(ATTRIB_COLOR);
		VAO.vertexAttribPointer(ATTRIB_UV);
		VAO.vertexAttribPointer(ATTRIB_TEX);
	}

	public static void begin(int mode) {
		drawingMode = mode;
		vertex = 0;
		texturesCount = 0;
	}

	public static void draw() {
		if (texturesCount == 0) {
			draw(PROGRAM, null);
		} else if (texturesCount == 1) {
			draw(PROGRAM_TEX, program -> {
				GL.bindTextureUnit(0, textures[0]);
			});
		} else {
			draw(PROGRAM_MULTITEX, TEX_BINDER_MULTI);
		}
	}

	public static void draw(ShaderProgram program, ITexBinder texBinder) {
		Renderer.debugGL("Drawer start", true);

		ATTRIB_POS.updateServerData();
		ATTRIB_COLOR.updateServerData();
		
		if(texturesCount > 0) {
			ATTRIB_UV.updateServerData();
			ATTRIB_TEX.updateServerData();
		}

		Renderer.debugGL("Drawer pre vao bind");

		program.use();
		VAO.bind();
		
		if(texBinder != null) texBinder.bind(program);
		
		GL11.glDrawArrays(drawingMode, 0, vertex);
		Renderer.debugGL("Draw drawArrays");
	}

	public static void texture(Texture2D tex) {
		textures[texturesCount++] = tex;
	}

	public static void vertex(float x, float y) {
		vertex(x, y, 0);
	}

	public static void vertex(float x, float y, float z) {
		ATTRIB_POS.putFloatF(vertex * 3, x).putFloatF(vertex * 3 + 1, y).putFloatF(vertex * 3 + 2, z);
		ATTRIB_COLOR.putIntI(vertex, COLOR.hex());
		
		if(texturesCount > 0) {
			ATTRIB_UV.putFloatF(vertex * 2, UV.x).putFloatF(vertex * 2 + 1, UV.y);
			ATTRIB_TEX.putShortS(vertex, (short) (texturesCount - 1));
		}
		
		vertex++;
	}
	
	public static interface ITexBinder {
		void bind(ShaderProgram program);
	}
}

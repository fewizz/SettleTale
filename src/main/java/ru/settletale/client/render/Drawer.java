package ru.settletale.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.TextureAbstract;
import ru.settletale.client.opengl.VertexArrayObject;
import ru.settletale.client.opengl.VertexBufferObject;
import ru.settletale.client.opengl.BufferObject.Usage;
import ru.settletale.client.opengl.Shader.Type;
import ru.settletale.client.vertex.PrimitiveArray;
import ru.settletale.client.vertex.PrimitiveArray.Storage;

public class Drawer {
	static final int POSITION = 0;
	static final int COLOR = 1;
	static final int UV = 2;
	static PrimitiveArray pa;
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
		pa = new PrimitiveArray(Storage.FLOAT_3, Storage.BYTE_4, Storage.FLOAT_2);
		program = new ShaderProgram().gen();
		program.attachShader(new Shader(Type.VERTEX, "shaders/default_vs.shader").gen().compile());
		program.attachShader(new Shader(Type.FRAGMENT, "shaders/default_fs.shader").gen().compile());
		program.link();
		programTex = new ShaderProgram().gen();
		programTex.attachShader(new Shader(Type.VERTEX, "shaders/default_vs_tex1.shader").gen().compile());
		programTex.attachShader(new Shader(Type.FRAGMENT, "shaders/default_fs_tex1.shader").gen().compile());
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
		pa.clear();
	}

	public static void draw() {
		draw(useTexture ? programTex : program);
	}
	public static void draw(ShaderProgram program) {
		GL.debug("Drawer start", true);

		positionBuffer.buffer(pa.getBuffer(POSITION)).loadData();
		colorBuffer.buffer(pa.getBuffer(COLOR)).loadData();

		vao.vertexAttribPointer(positionBuffer, 0, 3, GL11.GL_FLOAT, false, 0);
		vao.enableVertexAttribArray(0);

		vao.vertexAttribPointer(colorBuffer, 1, 4, GL11.GL_UNSIGNED_BYTE, true, 0);
		vao.enableVertexAttribArray(1);

		GL.debug("Drawer pre vao bind");

		if (useTexture) {
			uvBuffer.buffer(pa.getBuffer(UV)).loadData();
			vao.vertexAttribPointer(uvBuffer, 2, 2, GL11.GL_FLOAT, false, 0);
			vao.enableVertexAttribArray(2);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			texture.bind();
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
		pa.data(POSITION, x, y, z);
		pa.data(COLOR, r, g, b, a);
		if (useTexture)
			pa.data(UV, u, v);
		pa.endVertex();
		vertexCount++;
	}

	public static void texture(TextureAbstract<?> texture) {
		Drawer.texture = texture;
		useTexture = true;
	}
}

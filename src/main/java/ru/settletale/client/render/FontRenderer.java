package ru.settletale.client.render;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.resource.FontLoader;
import ru.settletale.client.resource.ShaderLoader;

public class FontRenderer {
	static final Vector3f POSITION = new Vector3f(0);
	static Font font = getDefaultFont();
	static float scale = 1F;
	static final Color COLOR = new Color(1F, 1F, 1F, 1F);
	static String text;
	static final ShaderProgram DEFAULT_PROGRAM = new ShaderProgram();
	static ShaderProgram program = DEFAULT_PROGRAM;

	public static Font getDefaultFont() {
		return FontLoader.FONTS.get("fonts/font.fnt");
	}

	public static void setFont(Font f) {
		float size = font == null ? f.originalSize : font.originalSize * scale;
		font = f;
		setSize(size);
	}

	public static void setScale(float scale) {
		FontRenderer.scale = scale;
	}

	public static void setSize(float size) {
		FontRenderer.scale = size / font.originalSize;
	}

	public static Color getColor() {
		return COLOR;
	}

	public static void setColor(Color c) {
		COLOR.set(c);
	}

	public static Vector3f getPosition() {
		return POSITION;
	}

	public static void setPosition(Vector3f v) {
		POSITION.set(v);
	}

	public static void setText(String text) {
		FontRenderer.text = text;
	}

	public static void renderCentered() {
		render(true);
	}

	public static void render() {
		render(false);
	}

	public static void render(boolean centered) {
		linkDefaultProgramIfNeed();

		float wTotal = POSITION.x + (centered ? -(font.getStringWidth(text) * scale) / 2F : 0);
		float y = POSITION.y + (centered ? -(font.originalSize * scale) / 2F : 0);

		GL.debug("Font start");
		Drawer.begin(GL11.GL_QUADS);

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			FontChar fch = font.getChar(ch);

			if (fch == null)
				continue;

			float tu = fch.x / (float) fch.page.texture.width;
			float tw = fch.width / (float) fch.page.texture.width;
			float tv = 1F - (fch.y / (float) fch.page.texture.height);
			float th = fch.height / (float) fch.page.texture.height;
			float xOffset = fch.xOffset * scale;
			float yOffset = fch.yOffset * scale;
			float base = font.base * scale;
			float width = fch.width * scale;
			float height = fch.height * scale;

			Texture2D tex = fch.page.texture;
			Drawer.texture(tex);
			Drawer.COLOR.set(COLOR);

			Drawer.UV.set(tu, tv - th);
			Drawer.vertex(wTotal + xOffset, (y + base - yOffset) - height, 0);

			Drawer.UV.set(tu, tv);
			Drawer.vertex(wTotal + xOffset, y + base - yOffset, 0);

			Drawer.UV.set(tu + tw, tv);
			Drawer.vertex(wTotal + xOffset + width, y + base - yOffset, 0);

			Drawer.UV.set(tu + tw, tv - th);
			Drawer.vertex(wTotal + xOffset + width, (y + base - yOffset) - height, 0);

			wTotal += fch.xAdvance * scale;
		}

		GL.debug("Font predraw");
		
		Drawer.draw(program);

		GL.debug("Font end");
	}

	static void linkDefaultProgramIfNeed() {
		if (!DEFAULT_PROGRAM.isGenerated()) {
			DEFAULT_PROGRAM.gen();
			DEFAULT_PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/font.vs"));
			DEFAULT_PROGRAM.attachShader(ShaderLoader.SHADERS.get("shaders/font.fs"));
			DEFAULT_PROGRAM.link();
		}
	}
}

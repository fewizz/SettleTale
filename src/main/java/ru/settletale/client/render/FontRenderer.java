package ru.settletale.client.render;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.ShaderProgram;
import ru.settletale.client.resource.ShaderLoader;

public class FontRenderer {
	static Vector3f position = new Vector3f(0);
	static Font font;
	static float scale = 1F;
	static Color color = new Color(1F, 1F, 1F, 1F);
	static String text;
	static final ShaderProgram defaultProgram = new ShaderProgram();
	static ShaderProgram program = defaultProgram;
	
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
		return color;
	}
	
	public static void setColor(Color c) {
		color = c;
	}
	
	public static Vector3f getPosition() {
		return position;
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
		
		float wTotal = position.x + (centered ? -(font.getStringWidth(text) * scale) / 2F : 0);
		float y = position.y + (centered ? -(font.originalSize * scale) / 2F : 0);
		
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			FontChar fch = font.getChar(ch);

			if (fch == null) {
				continue;
			}

			GL.debug("Font start");
			Drawer.begin(GL11.GL_QUADS);
			Drawer.texture(fch.page.texture);

			float tu = fch.x / (float)fch.page.texture.width;
			float tw = fch.width / (float)fch.page.texture.width;
			float tv = 1F - (fch.y / (float)fch.page.texture.height);
			float th = fch.height / (float)fch.page.texture.height;
			float xOffset = fch.xOffset * scale;
			float yOffset = fch.yOffset * scale;
			float base = font.base * scale;
			float width = fch.width * scale;
			float height = fch.height * scale;
			
			Drawer.color(color);
			
			Drawer.uv(tu, tv - th);
			Drawer.vertex(wTotal + xOffset, (y + base - yOffset) - height, 0);

			Drawer.uv(tu, tv);
			Drawer.vertex(wTotal + xOffset, y + base - yOffset, 0);

			Drawer.uv(tu + tw, tv);
			Drawer.vertex(wTotal + xOffset + width, y + base - yOffset, 0);

			Drawer.uv(tu + tw, tv - th);
			Drawer.vertex(wTotal + xOffset + width, (y + base - yOffset) - height, 0);
			
			GL.debug("Font predraw");
			Drawer.draw(program);
			
			GL.debug("Font end");

			wTotal += fch.xAdvance * scale;
		}
	}
	
	static void linkDefaultProgramIfNeed() {
		if(!defaultProgram.isGenerated()) {
			program = new ShaderProgram().gen();
			program.attachShader(ShaderLoader.SHADERS.get("shaders/font.vs"));
			program.attachShader(ShaderLoader.SHADERS.get("shaders/font.fs"));
			program.link();
		}
	}
}

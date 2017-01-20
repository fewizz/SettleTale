package ru.settletale.client;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.opengl.GL;
import ru.settletale.client.opengl.Shader;
import ru.settletale.client.opengl.ShaderProgram;
import ru.settletale.client.opengl.Shader.Type;
import ru.settletale.client.render.Drawer;

public class Font {
	public String name;
	public int origSize;
	public int charsCount;
	public int pageCount;
	public float base;
	public FontPage[] pages;
	static ShaderProgram program;

	public FontPage getPage(int index) {
		return pages[index];
	}

	public void render(String text, float x, float y) {
		if(program == null) {
			program = new ShaderProgram().gen();
			program.attachShader(new Shader(Type.VERTEX, "shaders/font_vs.shader").gen().compile());
			program.attachShader(new Shader(Type.FRAGMENT, "shaders/font_fs.shader").gen().compile());
			program.link();
		}
		
		float wTotal = x;

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			FontChar fch = getChar(ch);

			if (fch == null) {
				continue;
			}

			GL.debug("Font start");
			Drawer.begin(GL11.GL_QUADS);
			Drawer.color(1, 1, 1, 1);
			Drawer.texture(fch.page.texture);

			float tu = fch.x / (float)fch.page.texture.width;
			float tw = fch.width / (float)fch.page.texture.width;
			float tv = 1 - (fch.y / (float)fch.page.texture.height);
			float th = fch.height / (float)fch.page.texture.height;
			Drawer.uv(tu, tv - th);
			Drawer.vertex(fch.xOffset + wTotal, base - fch.yOffset - fch.height + y, 0);

			Drawer.uv(tu, tv);
			Drawer.vertex(fch.xOffset + wTotal, base - fch.yOffset + y, 0);

			Drawer.uv(tu + tw, tv);
			Drawer.vertex(fch.xOffset + wTotal + fch.width, base - fch.yOffset + y, 0);

			Drawer.uv(tu + tw, tv - th);
			Drawer.vertex(fch.xOffset + wTotal + fch.width, base - fch.yOffset - fch.height + y, 0);
			
			GL.debug("Font predraw");
			Drawer.draw(program);
			
			GL.debug("Font end");

			wTotal += fch.xAdvance;
		}
	}

	private FontChar getChar(char ch) {
		for (FontPage p : pages) {
			FontChar fch = p.getChar(ch);

			if (fch != null) {
				return fch;
			}

			continue;
		}

		return null;
	}
}

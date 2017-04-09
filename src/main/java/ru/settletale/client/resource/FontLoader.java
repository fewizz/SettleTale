package ru.settletale.client.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.settletale.client.render.Font;
import ru.settletale.client.render.FontChar;
import ru.settletale.client.render.FontPage;

public class FontLoader extends ResourceLoaderAbstract {
	public static final Map<String, Font> FONTS = new HashMap<>();
	
	@Override
	public String[] getRequiredExtensions() {
		return new String[] {"fnt"};
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading font: " + resourceFile.key);
		
		Font font = new Font();

		try (FileReader fr = new FileReader(resourceFile.path.toFile()); BufferedReader reader = new BufferedReader(fr)) {

			for (;;) {
				String line = reader.readLine();

				if (line == null) {
					break;
				}

				String[] lines = line.replaceAll(" +", " ").split(" ");
				
				switch(lines[0]) {
					case "info" :
						readInfo(font, lines);
						break;
					case "common" :
						readCommon(font, lines);
						break;
					case "page" :
						readPage(font, lines);
						break;
					case "chars" :
						readChars(font, lines);
						break;
					case "char" :
						readChar(font, lines);
						break;
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(FontPage page : font.pages) {
			ResourceFile res = resourceFile.dir.getResourceFile(page.textureName);
			ResourceManager.loadResource(res);
			page.texture = TextureLoader.TEXTURES.get(res.key);
		}
		
		FONTS.put(resourceFile.key, font);
	}
	
	static String getValue(String str) {
		return str.split("=")[1];
	}
	
	static int getValueInt(String str) {
		return Integer.parseInt(getValue(str));
	}
	
	static String getValueString(String str) {
		return getValue(str).replace("\"", "");
	}

	static void readInfo(Font font, String[] strings) {
		font.name = getValueString(strings[1]);
		font.originalSize = getValueInt(strings[2]);
	}
	
	static void readCommon(Font font, String[] strings) {
		font.pageCount = getValueInt(strings[5]);
		font.pages = new FontPage[font.pageCount];
		font.base = getValueInt(strings[1]);
	}

	static void readPage(Font font, String[] strings) {
		FontPage page = new FontPage();
		
		page.id = getValueInt(strings[1]);
		page.textureName = getValueString(strings[2]);

		font.pages[page.id] = page;

	}
	
	static void readChars(Font font, String[] strings) {
		font.charsCount = getValueInt(strings[1]);
	}
	
	static void readChar(Font font, String[] strings) {
		FontChar fc = new FontChar();
		fc.id = (char) getValueInt(strings[1]);
		fc.x = getValueInt(strings[2]);
		fc.y = getValueInt(strings[3]);
		fc.width = getValueInt(strings[4]);
		fc.height = getValueInt(strings[5]);
		fc.xOffset = getValueInt(strings[6]);
		fc.yOffset = getValueInt(strings[7]);
		fc.xAdvance = getValueInt(strings[8]);
		font.getPage(getValueInt(strings[9])).addChar(fc);
	}
}

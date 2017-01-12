package ru.settletale.client.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.settletale.client.Font;
import ru.settletale.client.FontChar;
import ru.settletale.client.FontPage;

public class FontLoader extends ResourceLoaderAbstract {
	final List<Font> fontList = new ArrayList<>();
	
	@Override
	public String getRequiredExtension() {
		return "fnt";
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		Font font = new Font();

		try (FileReader fr = new FileReader(resourceFile.fullPath); BufferedReader reader = new BufferedReader(fr)) {

			for (;;) {
				String line = reader.readLine();

				if (line == null) {
					break;
				}

				String[] lines = line.split(" ");
				
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
		
		fontList.add(font);

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
		font.origSize = getValueInt(strings[2]);
	}
	
	static void readCommon(Font font, String[] strings) {
		font.pageCount = getValueInt(strings[5]);
		font.pages = new FontPage[font.pageCount];
	}

	static void readPage(Font font, String[] strings) {
		FontPage page = new FontPage();
		
		page.id = getValueInt(strings[1]);
		page.texture = getValueString(strings[2]);

		font.pages[page.id] = page;

	}
	
	static void readChars(Font font, String[] strings) {
		font.charsCount = getValueInt(strings[1]);
	}
	
	static void readChar(Font font, String[] strings) {
		FontChar fc = new FontChar();
		fc.id = (short) getValueInt(strings[1]);
		fc.x = getValueInt(strings[2]);
		fc.y = getValueInt(strings[3]);
		fc.width = getValueInt(strings[4]);
		fc.height = getValueInt(strings[5]);
		fc.xOffset = getValueInt(strings[6]);
		fc.yOffset = getValueInt(strings[7]);
		fc.xAdvance = getValueInt(strings[8]);
		fc.page = (byte) getValueInt(strings[9]);
		
		FontPage page = font.getPage(fc.page);
		page.addChar(fc);
	}
}

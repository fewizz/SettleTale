package ru.settletale.client.render;

public class Font {
	public String name;
	public float originalSize;
	public int charsCount;
	public int pageCount;
	public float base;
	public FontPage[] pages;

	public FontPage getPage(int index) {
		return pages[index];
	}

	public FontChar getChar(char ch) {
		for (FontPage p : pages) {
			FontChar fch = p.getChar(ch);

			if (fch != null) {
				return fch;
			}

			continue;
		}

		return null;
	}

	public float getStringWidth(String str) {
		float wTotal = 0;

		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			FontChar fch = getChar(ch);

			if (fch == null) {
				continue;
			}

			wTotal += fch.xAdvance;
		}

		return wTotal;
	}
}

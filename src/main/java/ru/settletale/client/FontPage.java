package ru.settletale.client;

import com.koloboke.collect.map.hash.HashShortObjMap;
import com.koloboke.collect.map.hash.HashShortObjMaps;

public class FontPage {
	public int id;
	public String texture;
	final HashShortObjMap<FontChar> charMap;
	
	public FontPage() {
		charMap = HashShortObjMaps.newMutableMap();
	}
	
	public void addChar(FontChar ch) {
		charMap.put(ch.id, ch);
	}
	
	public FontChar getChar(short id) {
		return charMap.get(id);
	}
}

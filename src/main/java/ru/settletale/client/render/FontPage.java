package ru.settletale.client.render;

import com.koloboke.collect.map.hash.HashCharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

import ru.settletale.client.gl.Texture2D;

public class FontPage {
	public int id;
	public String textureName;
	final HashCharObjMap<FontChar> charMap;
	public Texture2D texture;
	
	public FontPage() {
		charMap = HashCharObjMaps.newMutableMap();
	}
	
	public void addChar(FontChar ch) {
		charMap.put(ch.id, ch);
		ch.page = this;
	}
	
	public FontChar getChar(char id) {
		return charMap.get(id);
	}
}

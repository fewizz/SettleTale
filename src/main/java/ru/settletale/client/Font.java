package ru.settletale.client;

public class Font {
	public String name;
	public int origSize;
	public int charsCount;
	public int pageCount;
	public FontPage[] pages;
	
	public FontPage getPage(int index) {
		return pages[index];
	}
	
}

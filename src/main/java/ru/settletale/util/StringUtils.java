package ru.settletale.util;

public class StringUtils {
	public static boolean isSharpCommentLine(String str) {
		return str.startsWith("#");
	}
	
	public static boolean isEmptyOrSpacedLine(String str) {
		if(str.isEmpty()) {
			return true;
		}
		
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) != ' ') {
				return false;
			}
		}
		
		return false;
	}
}

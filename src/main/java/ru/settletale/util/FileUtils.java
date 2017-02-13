package ru.settletale.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

import org.lwjgl.system.MemoryUtil;

public class FileUtils {
	static final CharBuffer BUFFER = MemoryUtil.memAlloc(0xFFFF).asCharBuffer();

	public static int getLineCount(File f) {
		try (FileReader fr = new FileReader(f)) {
			return ReaderUtils.getLineCount(fr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return -1;
	}
	
	public static String[] readLines(File f) {
		try (FileReader fr = new FileReader(f); FileReader fr2 = new FileReader(f);) {
			return ReaderUtils.readLines(fr2, ReaderUtils.getLineCount(fr));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public static String readWholeLines(File f) {
		try (FileReader fr = new FileReader(f);) {
			return ReaderUtils.readWholeLines(fr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
}

package ru.settletale.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class FileUtils {

	public static int getLineCount(File f) {
		try (FileReader fr = new FileReader(f)) {
			return ReaderUtils.getLineCount(fr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return -1;
	}
	
	public static List<String> readLines(File f) {
		try (FileReader fr = new FileReader(f)) {
			return ReaderUtils.readLines(fr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public static String readAsOneString(File f) {
		try (FileReader fr = new FileReader(f);) {
			return ReaderUtils.readAsOneString(fr);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return null;
	}
	
	public static String getDirectoryPath(File file) {
		String path = file.isDirectory() ? file.getPath() : file.getParent();
		return path.replace("\\", "/") + "/";
	}
}

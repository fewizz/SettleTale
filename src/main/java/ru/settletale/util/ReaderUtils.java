package ru.settletale.util;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class ReaderUtils {
	static final char[] BUFFER = new char[16384];

	public static int getLineCount(Reader reader) {
		int newLineCount = 0;

		try {
			for (;;) {
				int count = reader.read(BUFFER);

				if (count == -1) { // If this is end of file
					return newLineCount + 1;
				}

				for (int index = 0; index < count;) {
					char ch = BUFFER[index];
					
					int skip = skipNewLine(ch, index, count);
					
					if (skip > 0) { // If new line
						index += skip;
						newLineCount++;
					}
					else {
						index++;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static List<String> readLines(Reader reader) {
		try {
			List<String> strings = new ArrayList<>();

			StringBuilder builder = new StringBuilder();

			for (;;) {
				int count = reader.read(BUFFER);

				if (count == -1) {
					if(!strings.isEmpty())
						strings.add(builder.toString());
					
					return strings;
				}

				for (int index = 0; index < count;) {
					char ch = BUFFER[index];

					int skip = skipNewLine(ch, index, count);

					if (skip > 0) { // If new line
						index += skip;

						strings.add(builder.toString());
						
						builder.setLength(0);
					}
					else {
						builder.append(ch);
						index++;
					}

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String readAsOneString(Reader reader) {
		try {
			StringBuilder builder = new StringBuilder();

			for (;;) {
				int count = reader.read(BUFFER);

				if (count == -1) { // If this is end of file
					return builder.toString();
				}

				builder.append(BUFFER, 0, count);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	static int skipNewLine(char mainChar, int index, int max) {
		if (mainChar == '\r') {
			if (index + 1 < max && BUFFER[index + 1] == '\n') {
				return 2;
			}

			return 1;
		}
		if (mainChar == '\n') {
			return 1;
		}

		return 0;
	}
}

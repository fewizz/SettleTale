package ru.settletale.client.resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import ru.settletale.client.opengl.Texture2D;

public class TextureLoader {
	private static final Map<String, Texture2D> texturesToRegister = new HashMap<>();
	public static final Map<String, Texture2D> textures = new HashMap<>();

	static void loadTexture(String id, Path path) {
		System.out.println("Loading texture: " + id);
		
		BufferedImage image = null;
		
		try {
			File fileToRead = new File(path.toString() + "/", id);
			image = ImageIO.read(fileToRead);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		int size = width * height;
		ByteBuffer buffer = ByteBuffer.allocateDirect(size * 4);
		
		int index = 0;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				int color = image.getRGB(x, height - y - 1);
				int r = (color >>> 16) & 0xFF;
				int g = (color >>> 8) & 0xFF;
				int b = color & 0xFF;
				int a = (color >>> 24) & 0xFF;
				
				buffer.putInt(index, (r << 24) | (g << 16) | (b << 8) | a);
				index += 4;
			}
		}
		
		Texture2D tex = new Texture2D(width, height);
		tex.buffer = buffer;

		texturesToRegister.put(id, tex);
	}
	
	static void registerTextures() {
		for(Map.Entry<String, Texture2D> entry : texturesToRegister.entrySet()) {
			Texture2D tex = entry.getValue();
			tex.gen().setDefaultParams();
			tex.data();
			
			textures.put(entry.getKey(), tex);
		}
		
		texturesToRegister.clear();
	}
}

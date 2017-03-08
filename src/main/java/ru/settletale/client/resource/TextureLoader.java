package ru.settletale.client.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.render.GLThread;

public class TextureLoader extends ResourceLoaderAbstract {
	public static final Map<String, Texture2D> TEXTURES = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] {"png", "jpg"};
	}
	
	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading texture: " + resourceFile.key);

		BufferedImage image = null;

		try {
			image = ImageIO.read(resourceFile.fullPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		int width = image.getWidth();
		int height = image.getHeight();
		int size = width * height;
		ByteBuffer buffer = ByteBuffer.allocateDirect(size * 4);

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
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
		tex.bufferType = GL11.GL_UNSIGNED_BYTE;
		
		GLThread.addTask(() -> {
			tex.gen().setDefaultParams().loadData();
		});

		TEXTURES.put(resourceFile.key, tex);
	}
}

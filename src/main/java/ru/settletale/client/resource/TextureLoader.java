package ru.settletale.client.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.gl.GL;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.render.GLThread;

public class TextureLoader extends ResourceLoaderAbstract {
	public static final Map<String, Texture2D> TEXTURES = new HashMap<>();

	@Override
	public String[] getRequiredExtensions() {
		return new String[] { "png", "jpg" };
	}

	@Override
	public void loadResource(ResourceFile resourceFile) {
		System.out.println("Loading texture: " + resourceFile.key);

		BufferedImage image = null;

		try {
			image = ImageIO.read(resourceFile.fullPath);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		int width = image.getWidth();
		int height = image.getHeight();
		int size = width * height;
		ByteBuffer buffer = MemoryUtil.memAlloc(size * 4);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = image.getRGB(x, height - y - 1);
				buffer.putInt((color & 0xFF000000) | ((color << 16) & 0x00FF0000) | (color & 0x0000FF00) | ((color >>> 16) & 0xFF));
			}
		}
		buffer.flip();

		Texture2D tex = new Texture2D(width, height);

		GLThread.addTask(() -> {
			tex.gen().setDefaultParams().bufferDataFormat(GL11.GL_RGBA).bufferDataType(GL11.GL_UNSIGNED_BYTE).loadData(buffer);
			GL.debug("Load texture: " + resourceFile.key);
			MemoryUtil.memFree(buffer);
		});

		TEXTURES.put(resourceFile.key, tex);
	}
}

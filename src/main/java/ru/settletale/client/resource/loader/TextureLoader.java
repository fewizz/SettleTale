package ru.settletale.client.resource.loader;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import ru.settletale.client.Client;
import ru.settletale.client.gl.Texture2D;
import ru.settletale.client.render.Renderer;
import ru.settletale.client.resource.ResourceFile;
import ru.settletale.memory.MemoryBlock;

public class TextureLoader extends ResourceLoaderAbstract<Texture2D> {

	@Override
	public Texture2D loadResource(ResourceFile resourceFile) {
		System.out.println("Loading texture: " + resourceFile.key);

		BufferedImage image = null;

		try {
			image = ImageIO.read(resourceFile.getLeadPath().toFile());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		int width = image.getWidth();
		int height = image.getHeight();
		int size = width * height;
		
		MemoryBlock buffer = new MemoryBlock(size * 4);

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = image.getRGB(x, y);
				buffer.putIntI(index++, (color & 0xFF000000) | ((color << 16) & 0x00FF0000) | (color & 0x0000FF00) | ((color >>> 16) & 0xFF));
			}
		}
		
		Texture2D tex = new Texture2D();

		Runnable run = () -> {
			tex.gen().data2D(buffer, 0, GL11.GL_RGBA, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE);
			Renderer.debugGL("Load texture: " + resourceFile.key);
			buffer.free();
		};
		
		if(Thread.currentThread() != Client.GL_THREAD) {
			Client.GL_THREAD.addTask(run);
		}
		else {
			run.run();
		}
		
		return tex;
	}
}

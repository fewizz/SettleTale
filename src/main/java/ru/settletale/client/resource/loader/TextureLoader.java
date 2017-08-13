package ru.settletale.client.resource.loader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import ru.settletale.client.Client;
import ru.settletale.client.render.Renderer;
import ru.settletale.client.resource.ResourceFile;
import wrap.gl.Texture2D;

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
		ByteBuffer buffer = MemoryUtil.memAlloc(size * 4);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = image.getRGB(x, y);
				buffer.putInt((color & 0xFF000000) | ((color << 16) & 0x00FF0000) | (color & 0x0000FF00) | ((color >>> 16) & 0xFF));
			}
		}
		buffer.flip();

		Texture2D tex = new Texture2D(width, height);

		Runnable run = () -> {
			tex.gen().setDefaultParams().bufferDataFormat(GL11.GL_RGBA).bufferDataType(GL11.GL_UNSIGNED_BYTE).data(buffer);
			Renderer.debugGL("Load texture: " + resourceFile.key);
			MemoryUtil.memFree(buffer);
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

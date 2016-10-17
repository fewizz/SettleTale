package ru.settletale;

import java.io.IOException;

import ru.settletale.client.PlatformClient;
import ru.settletale.registry.Biomes;
import ru.settletale.registry.Tiles;
import ru.settletale.server.PlatformServer;
import ru.settletale.util.Side;
import ru.settletale.world.World;

public class Game {
	public static IPlatform platform;
	
	public static void main(String[] args) throws IOException {
		if(args != null && args.length == 1 && args[0].equals("--server")) {
			platform = new PlatformServer();
		}
		else {
			platform = new PlatformClient();
		}
		Biomes.register();
		Tiles.register();
		platform.start();
		
		/*
		
		BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
		Layer layer = 
				LayerSmoother.getLayer(6,
						LayerScaleX2Random.getLayer(1,
								LayerSmoother.getLayer(2,
										LayerScaleX2Random.getLayer(2,
												LayerSmoother.getLayer(1,
														LayerScaleX2Random.getLayer(4,
																new LayerBiomes()))))));
		byte[] array = layer.getValues(-1, 0, 128, 128);
		
		for(int x = 0; x < 128; x++) {
			for(int z = 0; z < 128; z++) {
				int value = array[z * 128 + x];
				image.setRGB(x, z, Biomes.getBiomeByID(value).color.getRGB());
			}
		}
		
		ImageIO.write(image, "png", new File("image.png"));*/
	}
	
	public static World getWorld() {
		return platform.getWorld();
	}
	
	public static Side getSide() {
		return platform.getSide();
	}
}

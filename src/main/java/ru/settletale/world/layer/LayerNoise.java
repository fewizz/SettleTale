package ru.settletale.world.layer;

import ru.settletale.registry.Biomes;

public class LayerNoise extends Layer {

	public LayerNoise(Layer parent) {
		super(parent);
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		byte values[] = getByteArray(width, length);//new byte[width * length];
		
		for(int x2 = 0; x2 < width; x2++) {
			for(int z2 = 0; z2 < length; z2++) {
				 double value = (((noise.eval(x2 / 25F, z2 / 25F) + 1) / 2D) * 127D);
				 if(value < 70) {
					 values[z2 * length + x2] = (byte) Biomes.sea.getBiomeID();
				 }
				 else {
					 values[z2 * length + x2] = (byte) Biomes.plain.getBiomeID();
				 }
			}
		}
		
		return values;
	}

}

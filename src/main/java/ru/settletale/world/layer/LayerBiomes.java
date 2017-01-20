package ru.settletale.world.layer;

import java.util.ArrayList;

import ru.settletale.registry.Biomes;
import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.biome.Climate;

public class LayerBiomes extends LayerAbstract {

	public LayerBiomes() {
		super(new LayerClimate());
	}

	@Override
	public byte[] getValues(int x, int z, int width, int length) {
		byte[] valuesParent = parent.getValues(x, z, width, length);
		byte[] values = getByteArray(width, length);//new byte[width * length];
		
		for(int x2 = 0; x2 < width; x2++) {
			for(int z2 = 0; z2 < length; z2++) {
				int id = z2 * length + x2;
				
				Climate climate = Climate.all()[valuesParent[id]];
				
				boolean earth = getPRInt(x + x2, z + z2, 100) >= climate.chanceOfWater;
				ArrayList<BiomeAbstract> biomesByClimate;
				
				if(earth) {
					biomesByClimate = Biomes.getEarthBiomesByClimate(climate);
				}
				else {
					biomesByClimate = Biomes.getOceanBiomesByClimate(climate);
				}
				
				int biomeID = biomesByClimate.get(getPRInt(x + x2, z + z2, biomesByClimate.size())).getBiomeID();
				
				values[id] = (byte) biomeID;
			}
		}
		
		return values;
	}

}

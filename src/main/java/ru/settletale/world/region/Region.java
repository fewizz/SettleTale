package ru.settletale.world.region;

import ru.settletale.registry.Biomes;
import ru.settletale.util.MathUtils;
import ru.settletale.world.biome.BiomeAbstract;

public class Region {
	public byte[] biomeIDs;
	public float[] heights;
	public int x;
	public int z;
	public boolean active;
	public long coord;
	public volatile int threads = 0;
	
	public Region(int x, int z) {
		initInfo(x, z);
	}
	
	public void initInfo(int x, int z) {
		this.x = x;
		this.z = z;
		active = false;
		this.coord = MathUtils.clamp(x, z);
	}
	
	public BiomeAbstract getBiome(int x, int z) {
		return Biomes.getBiomeByID( biomeIDs[(z + 1) * 18 + x + 1] );
	}
	
	public float getHeight(int x, int z) {
		return heights[z * ((18 * 2) + 1) + x];
	}
	
	public void setBiomes(byte[] ids) {
		if(ids.length != 18 * 18) {
			throw new Error("Array for biomes has invalid len!");
		}
		this.biomeIDs = ids;
	}
	
	public void setHeights(float[] array) {
		if(array.length != ((18 * 2) + 1) * ((18 * 2) + 1)) {
			throw new Error("Array for heights has invalid len!");
		}
		this.heights = array;
	}
	
	public void update() {
		
	}
}

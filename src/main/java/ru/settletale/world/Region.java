package ru.settletale.world;

import ru.settletale.registry.Biomes;
import ru.settletale.util.SSMath;

public class Region {
	private byte[] biomeIDs;
	public int x;
	public int z;
	public boolean active;
	public long coord;
	RegionData chunkData;
	
	public Region(int x, int z) {
		this.x = x;
		this.z = z;
		active = false;
		this.coord = SSMath.clamp(x, z);
		biomeIDs = new byte[18 * 18];
		chunkData = new RegionData();
	}
	
	public Biome getBiome(int x, int z) {
		return Biomes.getBiomeByID( biomeIDs[(z + 1) * 18 + x + 1] );
	}
	
	public void setBiomes(byte[] ids) {
		if(ids.length < 18 * 18) {
			throw new Error("Array for biomes is too small!");
		}
		this.biomeIDs = ids;
	}
	
	public void update() {
		
	}
}

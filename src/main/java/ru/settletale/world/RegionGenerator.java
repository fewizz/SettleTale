package ru.settletale.world;

import ru.settletale.registry.Biomes;
import ru.settletale.util.OpenSimplexNoise;

public class RegionGenerator {
	public static final int CHUNK_LEN = 16;
	public static final int CHUNK_EXT = 1;
	public static final int CHUNK_LEN_EXT = CHUNK_LEN + CHUNK_EXT * 2;
	public static final int SMOOTH = 4;
	public static final int SMOOTH_EXT = SMOOTH + CHUNK_EXT;
	public static final int CHUNK_LEN_EXT_SMOOTH = CHUNK_LEN_EXT + (SMOOTH * 2);
	public static final int HEIGHTS_LEN_EXT = (CHUNK_LEN_EXT * 2) + 1;
	public static final int HEIGHTS_LEN_EXT_SMOOTH = (CHUNK_LEN_EXT_SMOOTH * 2) + 2;
	Layer mainLayer;
	OpenSimplexNoise noise = new OpenSimplexNoise();
	
	public RegionGenerator() {
		mainLayer = LayerSmoother.getLayer(6,
						LayerScaleX2Random.getLayer(1,
								LayerSmoother.getLayer(2,
										LayerScaleX2Random.getLayer(2,
												LayerSmoother.getLayer(1,
														LayerScaleX2Random.getLayer(4,
																new LayerBiomes()))))));
	}
	
	/** Temp varies **/
	static byte[] biomeIDs;
	static byte[] biomeIDsNorm;
	static float[] heightsOrgnl = new float[HEIGHTS_LEN_EXT_SMOOTH * HEIGHTS_LEN_EXT_SMOOTH];
	
	public Region generateRegion(int cx, int cz) {
		Region reg = new Region(cx, cz);
		biomeIDs = mainLayer.getValues((cx * 16) - SMOOTH_EXT, (cz * 16) - SMOOTH_EXT, CHUNK_LEN_EXT_SMOOTH + 1, CHUNK_LEN_EXT_SMOOTH + 1);
		biomeIDsNorm = new byte[CHUNK_LEN_EXT * CHUNK_LEN_EXT];
		float[] heights = new float[HEIGHTS_LEN_EXT * HEIGHTS_LEN_EXT];
		
		for(int x = -SMOOTH_EXT; x <= CHUNK_LEN_EXT_SMOOTH - SMOOTH_EXT; x++) {
			for(int z = -SMOOTH_EXT; z <= CHUNK_LEN_EXT_SMOOTH - SMOOTH_EXT; z++) {
				int index = (z + SMOOTH_EXT) * (CHUNK_LEN_EXT_SMOOTH + 1) + x + SMOOTH_EXT;
				
				int z3 = (z + SMOOTH_EXT) * 2;
				int x3 = (x + SMOOTH_EXT) * 2;
				
				int indexH1 = z3 * HEIGHTS_LEN_EXT_SMOOTH + x3;
				int indexH2 = (z3 + 1) * HEIGHTS_LEN_EXT_SMOOTH + x3;
				int indexH3 = (z3 + 1) * HEIGHTS_LEN_EXT_SMOOTH + (x3 + 1);
				int indexH4 = z3 * HEIGHTS_LEN_EXT_SMOOTH + (x3 + 1);
				
				byte biomeID = biomeIDs[index];
				Biome biome = Biomes.getBiomeByID(biomeID);
				
				float noiseVal;
				
				noiseVal = (float) ((noise.eval((cx * 16 + x) / 25F, (cz * 16 + z) / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH1] = biome.maxH + (noiseVal * biome.var);
				
				noiseVal = (float) ((noise.eval((cx * 16 + x) / 25F, (cz * 16 + 0.5F + z) / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH2] = biome.maxH + (noiseVal * biome.var);
				
				noiseVal = (float) ((noise.eval((cx * 16 + 0.5F + x) / 25F, (cz * 16 + 0.5F + z) / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH3] = biome.maxH + (noiseVal * biome.var);
				
				noiseVal = (float) ((noise.eval((cx * 16 + 0.5F + x) / 25F, (cz * 16 + z) / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH4] = biome.maxH + (noiseVal * biome.var);
				
				if(x >= -CHUNK_EXT && x < CHUNK_LEN + CHUNK_EXT && z >= -CHUNK_EXT && z < CHUNK_LEN + CHUNK_EXT) {
					biomeIDsNorm[(z + CHUNK_EXT) * CHUNK_LEN_EXT + (x + CHUNK_EXT)] = biomeID;
				}
			}
		}
		
		for(int x = -CHUNK_EXT * 2; x < HEIGHTS_LEN_EXT - CHUNK_EXT * 2; x++) {
			for(int z = -CHUNK_EXT * 2; z < HEIGHTS_LEN_EXT - CHUNK_EXT * 2; z++) {
				
				float centerValue = 0;
				int count = 0;
				
				for(int x2 = -SMOOTH * 2; x2 < SMOOTH * 2; x2++) {
					for(int z2 = -SMOOTH * 2; z2 < SMOOTH * 2; z2++) {
						int index = (z2 + SMOOTH * 2 + z + CHUNK_EXT * 2) * HEIGHTS_LEN_EXT_SMOOTH + (x2 + SMOOTH * 2 + x + CHUNK_EXT * 2);
						//index += HEIGHTS_LEN_EXT_SMOOTH + 1;
						centerValue += heightsOrgnl[index];
						count ++;
					}
				}
				
				int index = (z + CHUNK_EXT * 2) * HEIGHTS_LEN_EXT + (x + CHUNK_EXT * 2);
				
				heights[index] = centerValue / (float) count;
			}
		}
		
		
		reg.setHeights(heights);
		reg.setBiomes(biomeIDsNorm);
		return reg;
	}
}

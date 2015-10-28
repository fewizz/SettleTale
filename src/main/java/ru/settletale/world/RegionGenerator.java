package ru.settletale.world;

import ru.settletale.registry.Biomes;
import ru.settletale.util.OpenSimplexNoise;

public class RegionGenerator {
	public static final int SMOOTH = 4;
	public static final int SMOOTH_FULL = 18 + (SMOOTH * 2);
	public static final int HEIGHTS_LEN = (18 * 2) + 1;
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
	static byte[] biomeIDsNorm = new byte[18 * 18];
	static int heightsOLen = (SMOOTH_FULL + 1) * 2;
	static float[] heightsO = new float[heightsOLen * heightsOLen];
	static int heightsLen = (18 * 2) + 1;
	
	public Region generateRegion(int x, int z) {
		Region reg = new Region(x, z);
		biomeIDs = mainLayer.getValues((x * 16) - SMOOTH - 1, (z * 16) - SMOOTH - 1, SMOOTH_FULL + 1, SMOOTH_FULL + 1);
		biomeIDsNorm = new byte[18 * 18];
		float[] heights = new float[heightsLen * heightsLen];
		
		final int SMOOTH_P_O = SMOOTH + 1;
		
		for(int xS = -SMOOTH - 1; xS <= 16 + SMOOTH + 1; xS++) {
			for(int zS = -SMOOTH - 1; zS <= 16 + SMOOTH + 1; zS++) {
				int index = (zS + SMOOTH_P_O) * (SMOOTH_FULL + 1) + xS + SMOOTH_P_O;
				
				int indexH1 = ((zS + SMOOTH_P_O) * 2) * heightsOLen + ((xS + SMOOTH_P_O) * 2);
				int indexH2 = (((zS + SMOOTH_P_O) * 2) + 1) * heightsOLen + ((xS + SMOOTH_P_O) * 2);
				int indexH3 = (((zS + SMOOTH_P_O) * 2) + 1) * heightsOLen + (((xS + SMOOTH_P_O) * 2) + 1);
				int indexH4 = ((zS + SMOOTH_P_O) * 2) * heightsOLen + (((xS + SMOOTH_P_O) * 2) + 1);
				
				byte biomeID = biomeIDs[index];
				Biome biome = Biomes.getBiomeByID(biomeID);
				
				float noiseVal;
				
				noiseVal = (float) ((noise.eval((x * 16 + xS) / 25F, (z * 16 + zS) / 25F) + 1F)) / 2F;
				heightsO[indexH1] = biome.maxH + (noiseVal * biome.var);
				
				noiseVal = (float) ((noise.eval((x * 16 + xS) / 25F, (z * 16 + 0.5F + zS) / 25F) + 1F)) / 2F;
				heightsO[indexH2] = biome.maxH + (noiseVal * biome.var);
				
				noiseVal = (float) ((noise.eval((x * 16 + 0.5F + xS) / 25F, (z * 16 + 0.5F + zS) / 25F) + 1F)) / 2F;
				heightsO[indexH3] = biome.maxH + (noiseVal * biome.var);
				
				noiseVal = (float) ((noise.eval((x * 16 + 0.5F + xS) / 25F, (z * 16 + zS) / 25F) + 1F)) / 2F;
				heightsO[indexH4] = biome.maxH + (noiseVal * biome.var);
				
				if(xS >= -1 && xS < 17 && zS >= -1 && zS < 17) {
					biomeIDsNorm[(zS + 1) * 18 + (xS + 1)] = biomeID;
				}
			}
		}
		
		for(int xS = 0; xS < 33; xS++) {
			for(int zS = 0; zS < 33; zS++) {
				
				float centerValue = 0;
				int count = 0;
				
				for(int x2 = -SMOOTH * 2; x2 < SMOOTH * 2; x2++) {
					for(int z2 = -SMOOTH * 2; z2 < SMOOTH * 2; z2++) {
						int index = (z2 + (SMOOTH * 2) + zS) * heightsOLen + (x2 + (SMOOTH * 2) + xS);
						index += heightsOLen + 1;
						centerValue += heightsO[index];
						count ++;
					}
				}
				
				int index = zS  * HEIGHTS_LEN + xS;
				
				heights[index] = centerValue / (float) count;
			}
		}
		
		
		reg.setHeights(heights);
		reg.setBiomes(biomeIDsNorm);
		return reg;
	}
}

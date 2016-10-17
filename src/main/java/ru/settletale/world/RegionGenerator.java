package ru.settletale.world;

import ru.settletale.registry.Biomes;
import ru.settletale.registry.Tiles;
import ru.settletale.tile.data.DataContainerTerrain;
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
	
	public Region generateRegion(int x, int z) {
		Region reg = new Region(x, z);
		byte[] biomeIDs = mainLayer.getValues((x * 16) - SMOOTH - 1, (z * 16) - SMOOTH - 1, SMOOTH_FULL + 1, SMOOTH_FULL + 1);
		byte[] biomeIDsNorm = new byte[18 * 18];
		int heightsOLen = (SMOOTH_FULL + 1) * 2;
		float[] heightsO = new float[heightsOLen * heightsOLen];
		int heightsLen = (18 * 2) + 1;
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
		
		// 33, bcs (16 * 2) + 1
		for(int xS = 0; xS < 33; xS++) {
			for(int zS = 0; zS < 33; zS++) {
				
				float centerValue = 0;
				//float centerValue2 = 0;
				//float centerValue3 = 0;
				//float centerValue4 = 0;
				int count = 0;
				
				for(int x2 = -SMOOTH * 2; x2 < SMOOTH * 2; x2++) {
					for(int z2 = -SMOOTH * 2; z2 < SMOOTH * 2; z2++) {
						int index = (z2 + (SMOOTH * 2) + zS) * heightsOLen + (x2 + (SMOOTH * 2) + xS);
						index += heightsOLen + 1;
						//int index1 = (zS + z2 + SMOOTH * 2) * (SMOOTH_FULL * 2) + ((xS + x2 + SMOOTH) * 2);
						//int index2 = ((zS + z2 + SMOOTH * 2) + 1) * (SMOOTH_FULL * 2) + ((xS + x2 + SMOOTH) * 2);
						//int index3 = ((zS + z2 + SMOOTH * 2) + 1) * (SMOOTH_FULL * 2) + (((xS + x2 + SMOOTH) * 2) + 1);
						//int index4 = (zS + z2 + SMOOTH * 2) * (SMOOTH_FULL * 2) + (((xS + x2 + SMOOTH) * 2) + 1);
						centerValue += heightsO[index];
						//centerValue2 += heightsO[index2];
						//centerValue3 += heightsO[index3];
						//centerValue4 += heightsO[index4];
						count ++;
					}
				}
				
				int index = zS  * HEIGHTS_LEN + xS;
				//int index2 = ((zS * 2) + 1) * (SMOOTH_FULL * 2) + (xS * 2);
				//int index3 = ((zS * 2) + 1) * (SMOOTH_FULL * 2) + ((xS * 2) + 1);
				//int index4 = (zS * 2) * (SMOOTH_FULL * 2) + ((xS * 2) + 1);
				
				heights[index] = centerValue / (float) count;
				//heights[index2] = centerValue2 / (float) count;
				//heights[index3] = centerValue3 / (float) count;
				//heights[index4] = centerValue4 / (float) count;
			}
		}
		
		
		setBlocks(reg, heights);
		reg.setBiomes(biomeIDsNorm);
		return reg;
	}
	
	void setBlocks(Region r, float[] heights) {
		for(int x = 0; x < 16; x++) {
			for(int z = 0; z < 16; z++) {
				//float bx = r.x * 16 + x;
				//float bz = r.z * 16 + z;
				
				DataContainerTerrain c = (DataContainerTerrain) Tiles.grass.newDataContainer();
				
				int index = 0;
				for(int z2 = 0; z2 <= 2; z2++) {
					for(int x2 = 0; x2 <= 2; x2++) {
						c.buffer.putFloat(index, heights[((z * 2) + z2) * HEIGHTS_LEN + (x * 2 + x2)]);
						index += 4;
					}
				}
				//c.buffer.putFloat(0, );
				/*c.buffer.putFloat(0 * 4, (((float) noise.eval((bx + 0.0F) / 25F, (bz + 0.0F) / 25F)) + 1F) * 32F);
				c.buffer.putFloat(1 * 4, (((float) noise.eval((bx + 0.5F) / 25F, (bz + 0.0F) / 25F)) + 1F) * 32F);
				c.buffer.putFloat(2 * 4, (((float) noise.eval((bx + 1.0F) / 25F, (bz + 0.0F) / 25F)) + 1F) * 32F);
				c.buffer.putFloat(3 * 4, (((float) noise.eval((bx + 0.0F) / 25F, (bz + 0.5F) / 25F)) + 1F) * 32F);
				c.buffer.putFloat(4 * 4, (((float) noise.eval((bx + 0.5F) / 25F, (bz + 0.5F) / 25F)) + 1F) * 32F);
				c.buffer.putFloat(5 * 4, (((float) noise.eval((bx + 1.0F) / 25F, (bz + 0.5F) / 25F)) + 1F) * 32F);
				c.buffer.putFloat(6 * 4, (((float) noise.eval((bx + 0.0F) / 25F, (bz + 1.0F) / 25F)) + 1F) * 32F);
				c.buffer.putFloat(7 * 4, (((float) noise.eval((bx + 0.5F) / 25F, (bz + 1.0F) / 25F)) + 1F) * 32F);
				c.buffer.putFloat(8 * 4, (((float) noise.eval((bx + 1.0F) / 25F, (bz + 1.0F) / 25F)) + 1F) * 32F);*/

				/*c.buffer.putFloat(0 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);
				c.buffer.putFloat(1 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);
				c.buffer.putFloat(2 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);
				c.buffer.putFloat(3 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);
				c.buffer.putFloat(4 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);
				c.buffer.putFloat(5 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);
				c.buffer.putFloat(6 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);
				c.buffer.putFloat(7 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);
				c.buffer.putFloat(8 * 4, heights[((z * 2) + 0) * HEIGHTS_LEN + ((x * 2) + 0)]);*/
				
				r.chunkData.set(x, z, Tiles.grass);
				r.chunkData.setContainer(x, z, c);
			}
		}
	}
}

package ru.settletale.world.region;

import ru.settletale.Game;
import ru.settletale.registry.Biomes;
import ru.settletale.util.OpenSimplexNoise;
import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.layer.LayerAbstract;
import ru.settletale.world.layer.LayerBiomes;
import ru.settletale.world.layer.LayerScaleX2Random;
import ru.settletale.world.layer.LayerSmoother;

public class RegionGenerator {
	public static final int SMOOTH = 4;
	public static final int SMOOTH_EXTENDED = SMOOTH + Region.EXTENSION;
	public static final int SMOOTH_2 = SMOOTH * 2;
	public static final int SMOOTH_2_QUAD = SMOOTH_2 * SMOOTH_2;
	public static final int REGION_WIDTH_EXTENDED_SMOOTHED = Region.WIDTH_EXTENDED + SMOOTH_2;
	public static final int HEIGHTS_LEN_EXT = (Region.WIDTH_EXTENDED * 2) + 1;
	public static final int HEIGHTS_LEN_EXT_SMOOTH = (REGION_WIDTH_EXTENDED_SMOOTHED * 2) + 2;
	LayerAbstract mainLayer;
	OpenSimplexNoise noise;
	
	public RegionGenerator() {
		mainLayer = LayerSmoother.getLayer(2,
						LayerScaleX2Random.getLayer(2,
								LayerSmoother.getLayer(2,
										LayerScaleX2Random.getLayer(2,
												LayerSmoother.getLayer(1,
														LayerScaleX2Random.getLayer(4,
																new LayerBiomes()))))));
	}
	
	public void start() {
		noise = new OpenSimplexNoise(Game.getWorld().seed);
		LayerAbstract.seed = Game.getWorld().seed;
	}
	
	/** Temp varies **/
	static byte[] biomeIDs;
	static byte[] biomeIDsNorm;
	static float[] heightsOrgnl = new float[HEIGHTS_LEN_EXT_SMOOTH * HEIGHTS_LEN_EXT_SMOOTH];

	public Region generateRegion(int cx, int cz) {
		Region reg = Region.getFreeRegion(cx, cz);
		
		biomeIDs = mainLayer.getValues((cx * Region.WIDTH) - SMOOTH_EXTENDED, (cz * Region.WIDTH) - SMOOTH_EXTENDED, REGION_WIDTH_EXTENDED_SMOOTHED + 1, REGION_WIDTH_EXTENDED_SMOOTHED + 1);
		biomeIDsNorm = reg.biomeIDs != null ? reg.biomeIDs : new byte[Region.WIDTH_EXTENDED * Region.WIDTH_EXTENDED];
		float[] heights = reg.heights != null ? reg.heights : new float[HEIGHTS_LEN_EXT * HEIGHTS_LEN_EXT];

		for (int z = -SMOOTH_EXTENDED; z <= REGION_WIDTH_EXTENDED_SMOOTHED - SMOOTH_EXTENDED; z++) {
			int index = (z + SMOOTH_EXTENDED) * (REGION_WIDTH_EXTENDED_SMOOTHED + 1);

			for (int x = -SMOOTH_EXTENDED; x <= REGION_WIDTH_EXTENDED_SMOOTHED - SMOOTH_EXTENDED; x++) {

				int z3 = (z + SMOOTH_EXTENDED) * 2;
				int x3 = (x + SMOOTH_EXTENDED) * 2;

				int indexH1 = z3 * HEIGHTS_LEN_EXT_SMOOTH + x3;
				int indexH2 = (z3 + 1) * HEIGHTS_LEN_EXT_SMOOTH + x3;
				int indexH3 = indexH2++;
				int indexH4 = indexH1++;

				byte biomeID = biomeIDs[index];
				BiomeAbstract biome = Biomes.getBiomeByID(biomeID);

				float noiseVal;

				float xp = cx * Region.WIDTH + x;
				float zp = cz * Region.WIDTH + z;

				noiseVal = (float) ((noise.eval(xp / 25F, zp / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH1] = biome.minHeight + (noiseVal * biome.amplitude);

				noiseVal = (float) ((noise.eval(xp / 25F, (zp + 0.5F) / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH2] = biome.minHeight + (noiseVal * biome.amplitude);

				noiseVal = (float) ((noise.eval((xp + 0.5F) / 25F, (zp + 0.5F) / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH3] = biome.minHeight + (noiseVal * biome.amplitude);

				noiseVal = (float) ((noise.eval((xp + 0.5F) / 25F, zp / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH4] = biome.minHeight + (noiseVal * biome.amplitude);

				if (x >= -Region.EXTENSION && x < Region.WIDTH + Region.EXTENSION && z >= -Region.EXTENSION && z < Region.WIDTH + Region.EXTENSION) {
					biomeIDsNorm[(z + Region.EXTENSION) * Region.WIDTH_EXTENDED + (x + Region.EXTENSION)] = biomeID;
				}

				index++;
			}
		}

		for (int x = -Region.EXTENSION * 2; x < HEIGHTS_LEN_EXT - Region.EXTENSION * 2; x++) {
			int indxX = x + Region.EXTENSION * 2;

			for (int z = -Region.EXTENSION * 2; z < HEIGHTS_LEN_EXT - Region.EXTENSION * 2; z++) {
				int indxZ = z + Region.EXTENSION * 2;
				float centerValue = 0;
				int count = 0;

				for (int z2 = -SMOOTH_2; z2 < SMOOTH_2; z2++) {
					int zQuad = z2 * z2;
					
					int index = (z2 + SMOOTH_2 + indxZ) * HEIGHTS_LEN_EXT_SMOOTH + indxX;

					for (int x2 = -SMOOTH_2; x2 < SMOOTH_2; x2++) {

						float hyp = zQuad + (x2 * x2);

						if (hyp >= SMOOTH_2_QUAD) {
							continue;
						}

						centerValue += heightsOrgnl[index];
						count++;
						index++;
					}
				}

				int index = indxZ * HEIGHTS_LEN_EXT + indxX;

				heights[index] = centerValue / (float) count;
			}
		}

		reg.setHeights(heights);
		reg.setBiomes(biomeIDsNorm);
		return reg;
	}
}

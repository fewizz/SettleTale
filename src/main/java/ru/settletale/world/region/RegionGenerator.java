package ru.settletale.world.region;

import ru.settletale.SettleTaleStart;
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
	public static final int HEIGHTS_LEN_EXT = Region.WIDTH_EXTENDED + 1;
	public static final int TEMP_HEIGHTS_LEN = REGION_WIDTH_EXTENDED_SMOOTHED + 1;
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
		noise = new OpenSimplexNoise(SettleTaleStart.getWorld().seed);
		LayerAbstract.seed = SettleTaleStart.getWorld().seed;
	}
	
	/** Temp varies **/
	static byte[] biomeIDs;
	static byte[] biomeIDsNorm;
	static float[] heightsOrgnl = new float[TEMP_HEIGHTS_LEN * TEMP_HEIGHTS_LEN];

	public Region generateRegion(int cx, int cz) {
		Region reg = Region.getFreeRegion(cx, cz);
		
		biomeIDs = mainLayer.getValues((cx * Region.WIDTH) - SMOOTH_EXTENDED, (cz * Region.WIDTH) - SMOOTH_EXTENDED, TEMP_HEIGHTS_LEN, TEMP_HEIGHTS_LEN);
		biomeIDsNorm = reg.biomeIDs != null ? reg.biomeIDs : new byte[Region.WIDTH_EXTENDED * Region.WIDTH_EXTENDED];
		float[] heights = reg.heights != null ? reg.heights : new float[HEIGHTS_LEN_EXT * HEIGHTS_LEN_EXT];

		for (int z = -SMOOTH_EXTENDED; z <= REGION_WIDTH_EXTENDED_SMOOTHED - SMOOTH_EXTENDED; z++) {
			int index = (z + SMOOTH_EXTENDED) * (REGION_WIDTH_EXTENDED_SMOOTHED + 1);

			for (int x = -SMOOTH_EXTENDED; x <= REGION_WIDTH_EXTENDED_SMOOTHED - SMOOTH_EXTENDED; x++) {

				int indexH = ((z + SMOOTH_EXTENDED) * TEMP_HEIGHTS_LEN) + (x + SMOOTH_EXTENDED);

				byte biomeID = biomeIDs[index];
				BiomeAbstract biome = Biomes.getBiomeByID(biomeID);

				float noiseVal;

				float xp = cx * Region.WIDTH + x;
				float zp = cz * Region.WIDTH + z;

				noiseVal = (float) ((noise.eval(xp / 25F, zp / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH] = biome.minHeight + (noiseVal * biome.amplitude);

				if (x >= -Region.EXTENSION && x < Region.WIDTH + Region.EXTENSION && z >= -Region.EXTENSION && z < Region.WIDTH + Region.EXTENSION) {
					biomeIDsNorm[(z + Region.EXTENSION) * Region.WIDTH_EXTENDED + (x + Region.EXTENSION)] = biomeID;
				}

				index++;
			}
		}

		for (int x = -Region.EXTENSION; x < HEIGHTS_LEN_EXT - Region.EXTENSION; x++) {
			int indxX = x + Region.EXTENSION;

			for (int z = -Region.EXTENSION; z < HEIGHTS_LEN_EXT - Region.EXTENSION; z++) {
				int indxZ = z + Region.EXTENSION;
				float centerValue = 0;
				int count = 0;

				for (int z2 = -SMOOTH; z2 < SMOOTH; z2++) {
					int zQuad = z2 * z2;
					
					int index = (z2 + SMOOTH + indxZ) * TEMP_HEIGHTS_LEN + indxX;

					for (int x2 = -SMOOTH; x2 < SMOOTH; x2++) {

						float hyp = zQuad + (x2 * x2);

						if (hyp >= SMOOTH * SMOOTH) {
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

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
	public static final int CHUNK_LEN = 16;
	public static final int CHUNK_EXT = 1;
	public static final int CHUNK_LEN_EXT = CHUNK_LEN + CHUNK_EXT * 2;
	public static final int SMOOTH = 5;
	public static final int SMOOTH_EXT = SMOOTH + CHUNK_EXT;
	public static final int SMOOTH_2 = SMOOTH * 2;
	public static final int SMOOTH_2_QUAD = SMOOTH_2 * SMOOTH_2;
	public static final int CHUNK_LEN_EXT_SMOOTH = CHUNK_LEN_EXT + (SMOOTH * 2);
	public static final int HEIGHTS_LEN_EXT = (CHUNK_LEN_EXT * 2) + 1;
	public static final int HEIGHTS_LEN_EXT_SMOOTH = (CHUNK_LEN_EXT_SMOOTH * 2) + 2;
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
		Region reg = RegionCache.getOrCreateNewRegion(cx, cz);
		biomeIDs = mainLayer.getValues((cx * 16) - SMOOTH_EXT, (cz * 16) - SMOOTH_EXT, CHUNK_LEN_EXT_SMOOTH + 1, CHUNK_LEN_EXT_SMOOTH + 1);
		biomeIDsNorm = new byte[CHUNK_LEN_EXT * CHUNK_LEN_EXT];
		float[] heights = new float[HEIGHTS_LEN_EXT * HEIGHTS_LEN_EXT];

		for (int z = -SMOOTH_EXT; z <= CHUNK_LEN_EXT_SMOOTH - SMOOTH_EXT; z++) {
			int index = (z + SMOOTH_EXT) * (CHUNK_LEN_EXT_SMOOTH + 1);

			for (int x = -SMOOTH_EXT; x <= CHUNK_LEN_EXT_SMOOTH - SMOOTH_EXT; x++) {

				int z3 = (z + SMOOTH_EXT) * 2;
				int x3 = (x + SMOOTH_EXT) * 2;

				int indexH1 = z3 * HEIGHTS_LEN_EXT_SMOOTH + x3;
				int indexH2 = (z3 + 1) * HEIGHTS_LEN_EXT_SMOOTH + x3;
				int indexH3 = indexH2++;
				int indexH4 = indexH1++;

				byte biomeID = biomeIDs[index];
				BiomeAbstract biome = Biomes.getBiomeByID(biomeID);

				float noiseVal;

				float xp = cx * 16 + x;
				float zp = cz * 16 + z;

				noiseVal = (float) ((noise.eval(xp / 25F, zp / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH1] = biome.minHeight + (noiseVal * biome.amplitude);

				noiseVal = (float) ((noise.eval(xp / 25F, (zp + 0.5F) / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH2] = biome.minHeight + (noiseVal * biome.amplitude);

				noiseVal = (float) ((noise.eval((xp + 0.5F) / 25F, (zp + 0.5F) / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH3] = biome.minHeight + (noiseVal * biome.amplitude);

				noiseVal = (float) ((noise.eval((xp + 0.5F) / 25F, zp / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH4] = biome.minHeight + (noiseVal * biome.amplitude);

				if (x >= -CHUNK_EXT && x < CHUNK_LEN + CHUNK_EXT && z >= -CHUNK_EXT && z < CHUNK_LEN + CHUNK_EXT) {
					biomeIDsNorm[(z + CHUNK_EXT) * CHUNK_LEN_EXT + (x + CHUNK_EXT)] = biomeID;
				}

				index++;
			}
		}

		for (int x = -CHUNK_EXT * 2; x < HEIGHTS_LEN_EXT - CHUNK_EXT * 2; x++) {
			int indxX = x + CHUNK_EXT * 2;

			for (int z = -CHUNK_EXT * 2; z < HEIGHTS_LEN_EXT - CHUNK_EXT * 2; z++) {
				int indxZ = z + CHUNK_EXT * 2;
				float centerValue = 0;
				int count = 0;

				for (int z2 = -SMOOTH * 2; z2 < SMOOTH * 2; z2++) {
					int index = (z2 + SMOOTH * 2 + indxZ) * HEIGHTS_LEN_EXT_SMOOTH + indxX;

					for (int x2 = -SMOOTH * 2; x2 < SMOOTH * 2; x2++) {

						float hyp = (z2 * z2) + (x2 * x2);

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

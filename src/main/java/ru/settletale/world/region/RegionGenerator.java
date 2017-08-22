package ru.settletale.world.region;

import ru.settletale.registry.Biomes;
import ru.settletale.util.OpenSimplexNoise;
import ru.settletale.world.World;
import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.layer.LayerAbstract;
import ru.settletale.world.layer.LayerBiomes;
import ru.settletale.world.layer.LayerScaleX2Random;
import ru.settletale.world.layer.LayerSmoother;

public class RegionGenerator {
	public static final int SMOOTH = 4;
	public static final int SMOOTH_EXTENDED = SMOOTH + Chunk.EXTENSION;
	public static final int SMOOTH_2 = SMOOTH * 2;
	public static final int SMOOTH_2_QUAD = SMOOTH_2 * SMOOTH_2;
	public static final int REGION_WIDTH_EXTENDED_SMOOTHED = Chunk.WIDTH_EXTENDED + SMOOTH_2;
	public static final int HEIGHTS_LEN_EXT = Chunk.WIDTH_EXTENDED + 1;
	public static final int TEMP_HEIGHTS_LEN = REGION_WIDTH_EXTENDED_SMOOTHED + 1;
	final World world;
	LayerAbstract mainLayer;
	OpenSimplexNoise noise;
	
	public RegionGenerator(World world) {
		this.world = world;
		mainLayer = LayerSmoother.getLayer(2,
						LayerScaleX2Random.getLayer(2,
								LayerSmoother.getLayer(2,
										LayerScaleX2Random.getLayer(2,
												LayerSmoother.getLayer(1,
														LayerScaleX2Random.getLayer(4,
																new LayerBiomes()))))));
	}
	
	public void start() {
		noise = new OpenSimplexNoise(world.seed);
		LayerAbstract.seed = world.seed;
	}
	
	/** Temp varies **/
	static byte[] biomeIDs;
	static byte[] biomeIDsNorm;
	static float[] heightsOrgnl = new float[TEMP_HEIGHTS_LEN * TEMP_HEIGHTS_LEN];

	public Chunk generateChunk(int cx, int cz) {
		Chunk reg = Chunk.getFreeChunk(cx, cz);
		
		biomeIDs = mainLayer.getValues((cx * Chunk.WIDTH) - SMOOTH_EXTENDED, (cz * Chunk.WIDTH) - SMOOTH_EXTENDED, TEMP_HEIGHTS_LEN, TEMP_HEIGHTS_LEN);
		biomeIDsNorm = reg.biomeIDs != null ? reg.biomeIDs : new byte[Chunk.WIDTH_EXTENDED * Chunk.WIDTH_EXTENDED];
		float[] heights = reg.heights != null ? reg.heights : new float[HEIGHTS_LEN_EXT * HEIGHTS_LEN_EXT];

		for (int z = -SMOOTH_EXTENDED; z <= REGION_WIDTH_EXTENDED_SMOOTHED - SMOOTH_EXTENDED; z++) {
			int index = (z + SMOOTH_EXTENDED) * (REGION_WIDTH_EXTENDED_SMOOTHED + 1);

			for (int x = -SMOOTH_EXTENDED; x <= REGION_WIDTH_EXTENDED_SMOOTHED - SMOOTH_EXTENDED; x++) {

				int indexH = ((z + SMOOTH_EXTENDED) * TEMP_HEIGHTS_LEN) + (x + SMOOTH_EXTENDED);

				byte biomeID = biomeIDs[index];
				BiomeAbstract biome = Biomes.getBiomeByID(biomeID);

				float noiseVal;

				float xp = cx * Chunk.WIDTH + x;
				float zp = cz * Chunk.WIDTH + z;

				noiseVal = (float) ((noise.eval(xp / 25F, zp / 25F) + 1F)) / 2F;
				heightsOrgnl[indexH] = biome.minHeight + (noiseVal * biome.amplitude);

				if (x >= -Chunk.EXTENSION && x < Chunk.WIDTH + Chunk.EXTENSION && z >= -Chunk.EXTENSION && z < Chunk.WIDTH + Chunk.EXTENSION) {
					biomeIDsNorm[(z + Chunk.EXTENSION) * Chunk.WIDTH_EXTENDED + (x + Chunk.EXTENSION)] = biomeID;
				}

				index++;
			}
		}

		for (int x = -Chunk.EXTENSION; x < HEIGHTS_LEN_EXT - Chunk.EXTENSION; x++) {
			int indxX = x + Chunk.EXTENSION;

			for (int z = -Chunk.EXTENSION; z < HEIGHTS_LEN_EXT - Chunk.EXTENSION; z++) {
				int indxZ = z + Chunk.EXTENSION;
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

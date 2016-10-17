package ru.settletale.registry;

import java.util.ArrayList;
import java.util.List;

import ru.settletale.world.Biome;
import ru.settletale.world.BiomeDesert;
import ru.settletale.world.BiomeIce;
import ru.settletale.world.BiomeJungle;
import ru.settletale.world.BiomePlain;
import ru.settletale.world.BiomeSea;
import ru.settletale.world.BiomeTaiga;
import ru.settletale.world.BiomeType;
import ru.settletale.world.Climate;

public class Biomes {
	public static final Biome[] biomes = new Biome[0xFF];
	@SuppressWarnings("unchecked")
	private static final List<Biome>[] biomesEarthByClimate = new ArrayList[Climate.all().length];
	@SuppressWarnings("unchecked")
	private static final List<Biome>[] biomesOceanByClimate = new ArrayList[Climate.all().length];
	private static int lastBiomeID = 0;
	public static Biome sea;
	public static Biome plain;
	public static Biome jungle;
	public static Biome ice;
	public static Biome desert;
	public static Biome taiga;

	public static void register() {
		registerBiome(sea = new BiomeSea());
		registerBiome(plain = new BiomePlain());
		registerBiome(jungle = new BiomeJungle());
		registerBiome(ice = new BiomeIce());
		registerBiome(desert = new BiomeDesert());
		registerBiome(taiga = new BiomeTaiga());
	}

	private static void registerBiome(Biome biome) {
		biomes[lastBiomeID] = biome;
		lastBiomeID++;
		
		for(Climate climate : biome.climats) {
			addBiomeByCliamte(biome, climate);
		}
	}

	public static int getBiomeID(Biome biome) {
		for (int i = 0; i < biomes.length; i++) {
			if (biomes[i] == biome)
				return i;
		}

		try {
			throw new Exception("Biome not found.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static Biome getBiomeByID(int id) {
		Biome biome = biomes[id];

		if (biome != null) {
			return biome;
		}

		try {
			throw new Exception("Biome not found.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void addBiomeByCliamte(Biome biome, Climate climate) {
		int id = climate.ordinal();

		if (biome.type == BiomeType.EARTH) {
			if (biomesEarthByClimate[id] == null) {
				biomesEarthByClimate[id] = new ArrayList<Biome>();
			}

			biomesEarthByClimate[id].add(biome);
		}
		if (biome.type == BiomeType.OCEAN) {
			if (biomesOceanByClimate[id] == null) {
				biomesOceanByClimate[id] = new ArrayList<Biome>();
			}

			biomesOceanByClimate[id].add(biome);
		}
	}

	public static ArrayList<Biome> getEarthBiomesByClimate(Climate climate) {
		return (ArrayList<Biome>) biomesEarthByClimate[climate.ordinal()];
	}
	
	public static ArrayList<Biome> getOceanBiomesByClimate(Climate climate) {
		return (ArrayList<Biome>) biomesOceanByClimate[climate.ordinal()];
	}
}

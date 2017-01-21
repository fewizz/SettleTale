package ru.settletale.registry;

import java.util.ArrayList;
import java.util.List;

import ru.settletale.world.biome.BiomeAbstract;
import ru.settletale.world.biome.BiomeDesert;
import ru.settletale.world.biome.BiomeIce;
import ru.settletale.world.biome.BiomeJungle;
import ru.settletale.world.biome.BiomePlain;
import ru.settletale.world.biome.BiomeSea;
import ru.settletale.world.biome.BiomeTaiga;
import ru.settletale.world.biome.BiomeType;
import ru.settletale.world.biome.Climate;

public class Biomes {
	public static final BiomeAbstract[] BIOMES = new BiomeAbstract[0xFF];
	@SuppressWarnings("unchecked")
	private static final List<BiomeAbstract>[] biomesEarthByClimate = new ArrayList[Climate.all().length];
	@SuppressWarnings("unchecked")
	private static final List<BiomeAbstract>[] biomesOceanByClimate = new ArrayList[Climate.all().length];
	private static int lastBiomeID = 0;
	public static BiomeAbstract sea;
	public static BiomeAbstract plain;
	public static BiomeAbstract jungle;
	public static BiomeAbstract ice;
	public static BiomeAbstract desert;
	public static BiomeAbstract taiga;

	public static void register() {
		registerBiome(sea = new BiomeSea());
		registerBiome(plain = new BiomePlain());
		registerBiome(jungle = new BiomeJungle());
		registerBiome(ice = new BiomeIce());
		registerBiome(desert = new BiomeDesert());
		registerBiome(taiga = new BiomeTaiga());
	}

	private static void registerBiome(BiomeAbstract biome) {
		BIOMES[lastBiomeID] = biome;
		lastBiomeID++;
		
		for(Climate climate : biome.climats) {
			addBiomeByCliamte(biome, climate);
		}
	}

	public static int getBiomeID(BiomeAbstract biome) {
		for (int i = 0; i < BIOMES.length; i++) {
			if (BIOMES[i] == biome)
				return i;
		}

		try {
			throw new Exception("Biome not found.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return -1;
	}

	public static BiomeAbstract getBiomeByID(int id) {
		BiomeAbstract biome = BIOMES[id];

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

	public static void addBiomeByCliamte(BiomeAbstract biome, Climate climate) {
		int id = climate.ordinal();

		if (biome.type == BiomeType.EARTH) {
			if (biomesEarthByClimate[id] == null) {
				biomesEarthByClimate[id] = new ArrayList<BiomeAbstract>();
			}

			biomesEarthByClimate[id].add(biome);
		}
		if (biome.type == BiomeType.OCEAN) {
			if (biomesOceanByClimate[id] == null) {
				biomesOceanByClimate[id] = new ArrayList<BiomeAbstract>();
			}

			biomesOceanByClimate[id].add(biome);
		}
	}

	public static ArrayList<BiomeAbstract> getEarthBiomesByClimate(Climate climate) {
		return (ArrayList<BiomeAbstract>) biomesEarthByClimate[climate.ordinal()];
	}
	
	public static ArrayList<BiomeAbstract> getOceanBiomesByClimate(Climate climate) {
		return (ArrayList<BiomeAbstract>) biomesOceanByClimate[climate.ordinal()];
	}
}

package ru.settletale.world.biome;

import java.awt.Color;

public class BiomeDesert extends BiomeAbstract {

	public BiomeDesert() {
		super(Color.YELLOW, BiomeType.EARTH, Climate.DRY);
		this.minHeight = 32;
		this.amplitude = 5;
	}

}

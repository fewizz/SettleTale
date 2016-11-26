package ru.settletale.world.biome;

import java.awt.Color;

public class BiomeDesert extends Biome {

	public BiomeDesert() {
		super(Color.YELLOW, BiomeType.EARTH, Climate.DRY);
		this.minHeight = 36;
		this.amplitude = 5;
	}

}

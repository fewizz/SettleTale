package ru.settletale.world;

import java.awt.Color;

public class BiomeDesert extends Biome {

	public BiomeDesert() {
		super(Color.YELLOW, BiomeType.EARTH, Climate.DRY);
		this.maxH = 36;
		this.var = 5;
	}

}

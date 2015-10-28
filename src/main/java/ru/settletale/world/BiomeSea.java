package ru.settletale.world;

import java.awt.Color;

public class BiomeSea extends Biome {

	public BiomeSea() {
		super(Color.BLUE, BiomeType.OCEAN, Climate.CONTINENTAL, Climate.MODERATE, Climate.TROPICAL, Climate.DRY);
		this.maxH = 22;
		this.var = 8;
	}

}
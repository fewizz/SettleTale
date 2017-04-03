package ru.settletale.world.biome;

import java.awt.Color;

public class BiomeSea extends BiomeAbstract {

	public BiomeSea() {
		super(Color.DARK_GRAY, BiomeType.OCEAN, Climate.CONTINENTAL, Climate.MODERATE, Climate.TROPICAL, Climate.DRY);
		this.minHeight = 20;
		this.amplitude = 11;
	}

}

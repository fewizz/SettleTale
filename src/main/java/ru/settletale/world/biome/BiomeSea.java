package ru.settletale.world.biome;

import java.awt.Color;

public class BiomeSea extends Biome {

	public BiomeSea() {
		super(Color.DARK_GRAY, BiomeType.OCEAN, Climate.CONTINENTAL, Climate.MODERATE, Climate.TROPICAL, Climate.DRY);
		this.minHeight = 22;
		this.amplitude = 8;
	}

}

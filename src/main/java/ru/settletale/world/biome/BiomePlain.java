package ru.settletale.world.biome;

import java.awt.Color;

public class BiomePlain extends Biome {

	public BiomePlain() {
		super(new Color(50, 120, 70), BiomeType.EARTH, Climate.MODERATE);
		this.amplitude = 20;
	}

}

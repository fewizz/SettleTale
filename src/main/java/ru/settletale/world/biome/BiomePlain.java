package ru.settletale.world.biome;

import java.awt.Color;

public class BiomePlain extends BiomeAbstract {

	public BiomePlain() {
		super(new Color(50, 120, 70), BiomeType.EARTH, Climate.MODERATE);
		this.amplitude = 100;
	}

}

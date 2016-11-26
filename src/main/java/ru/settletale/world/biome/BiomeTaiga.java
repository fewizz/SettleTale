package ru.settletale.world.biome;

import java.awt.Color;

public class BiomeTaiga extends Biome {

	public BiomeTaiga() {
		super(Color.CYAN, BiomeType.EARTH, Climate.CONTINENTAL, Climate.MODERATE);
		this.amplitude = 20;
	}

}

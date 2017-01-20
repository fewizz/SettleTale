package ru.settletale.world.biome;

import java.awt.Color;

public class BiomeTaiga extends BiomeAbstract {

	public BiomeTaiga() {
		super(Color.CYAN, BiomeType.EARTH, Climate.CONTINENTAL, Climate.MODERATE);
		this.amplitude = 5;
		this.minHeight = 40;
	}

}

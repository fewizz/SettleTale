package ru.settletale.world.biome;

import java.awt.Color;

public class BiomeJungle extends BiomeAbstract {

	public BiomeJungle() {
		super(Color.GREEN, BiomeType.EARTH, Climate.TROPICAL);
		this.minHeight = 40;
		this.amplitude = 10;
	}

}

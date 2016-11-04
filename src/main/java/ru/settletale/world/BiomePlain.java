package ru.settletale.world;

import java.awt.Color;

public class BiomePlain extends Biome {

	public BiomePlain() {
		super(new Color(50, 120, 70), BiomeType.EARTH, Climate.MODERATE);
		this.var = 20;
	}

}

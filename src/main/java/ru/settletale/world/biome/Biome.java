package ru.settletale.world.biome;

import java.awt.Color;

import ru.settletale.registry.Biomes;

public abstract class Biome {
	public Color color;
	public Climate[] climats;
	public BiomeType type;
	private Integer id;
	public float minHeight = 36F;
	public float amplitude = 10F;
	
	public Biome(Color color, BiomeType type, Climate... climats) {
		this.climats = climats;
		this.type = type;
		this.color = color;
	}
	
	public int getBiomeID() {
		if(this.id == null) {
			int id = Biomes.getBiomeID(this);
			this.id = id;
			return id;
		}
		
		return this.id;
	}
}

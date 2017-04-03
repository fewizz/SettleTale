package ru.settletale.world.region;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import ru.settletale.registry.Biomes;
import ru.settletale.util.MathUtils;
import ru.settletale.world.biome.BiomeAbstract;

public class Region {
	static final Queue<Region> FREE_REGION_CACHE = new ArrayDeque<>();
	public static final int WIDTH = 32;
	public static final int EXTENSION = 1;
	public static final int WIDTH_F = WIDTH;
	public static final int WIDTH_EXTENDED = WIDTH + (EXTENSION * 2);
	public byte[] biomeIDs;
	public float[] heights;
	public int x;
	public int z;
	public boolean active;
	public long coord;
	private AtomicInteger threadsUses = new AtomicInteger(0);

	private Region() {
	}

	public static Region getFreeRegion(int x, int z) {
		Region r = FREE_REGION_CACHE.poll();
		if(r == null) {
			r = new Region();
		}
		if(r.threadsUses.get() != 0) {
			System.out.println("LOL!!!");
		}
		
		r.initInfo(x, z);
		r.increaseThreadUsage();
		return r;
	}

	public void increaseThreadUsage() {
		if(FREE_REGION_CACHE.contains(this)) {
			throw new Error("It's in cache");
		}
		threadsUses.incrementAndGet();
	}

	public void decreaseThreadUsage() {
		int val = threadsUses.decrementAndGet();

		if(val < 0) {
			throw new Error("Too many region thread usage decreases");
		}
		if(val == 0) {
			FREE_REGION_CACHE.add(this);
		}
	}

	public void initInfo(int x, int z) {
		this.x = x;
		this.z = z;
		active = false;
		this.coord = MathUtils.clamp(x, z);
	}

	public BiomeAbstract getBiome(int x, int z) {
		return Biomes.getBiomeByID(biomeIDs[(z + 1) * WIDTH_EXTENDED + x + 1]);
	}

	public float getHeight(int x, int z) {
		return heights[(z + 2) * ((WIDTH_EXTENDED * 2) + 1) + (x + 2)];
	}

	public void setBiomes(byte[] ids) {
		if (ids.length != WIDTH_EXTENDED * WIDTH_EXTENDED) {
			throw new Error("Array for biomes has invalid len!");
		}
		this.biomeIDs = ids;
	}

	public void setHeights(float[] array) {
		if (array.length != ((WIDTH_EXTENDED * 2) + 1) * ((WIDTH_EXTENDED * 2) + 1)) {
			throw new Error("Array for heights has invalid len!");
		}
		this.heights = array;
	}

	public void update() {

	}
}

package ru.settletale.world.region;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import ru.settletale.registry.Biomes;
import ru.settletale.util.MathUtils;
import ru.settletale.world.biome.BiomeAbstract;

public class Region {
	static final Queue<Region> FREE_REGION_CACHE = new ConcurrentLinkedQueue<Region>();
	public static final int WIDTH = 32;
	public static final int EXTENSION = 1;
	public static final int WIDTH_F = WIDTH;
	public static final int WIDTH_EXTENDED = WIDTH + (EXTENSION * 2);
	public byte[] biomeIDs;
	public float[] heights;
	public int x;
	public int z;
	public boolean active;
	public long coordClamped;
	public int hash;
	private AtomicInteger threadUsages = new AtomicInteger(0);

	private Region() {
	}

	public static Region getFreeRegion(int x, int z) {
		Region r = FREE_REGION_CACHE.peek();
		if (r == null) {
			r = new Region();
		}
		if (r.threadUsages.get() != 0) {
			throw new Error("Is already uses");
		}

		r.increaseThreadUsage();
		r.initInfo(x, z);
		return r;
	}

	public void increaseThreadUsage() {
		if(threadUsages.get() == 0 && FREE_REGION_CACHE.contains(this)) {
			FREE_REGION_CACHE.remove(this);
		}
		threadUsages.incrementAndGet();
	}

	public void decreaseThreadUsage() {
		int val = threadUsages.decrementAndGet();

		if (val < 0) {
			throw new Error("Too many region thread usage decreases");
		}
		if (val == 0) {
			FREE_REGION_CACHE.add(this);
		}
	}

	public void initInfo(int x, int z) {
		this.x = x;
		this.z = z;
		active = false;
		this.coordClamped = MathUtils.clampLong(x, z);
		this.hash = MathUtils.clampInt(x, z);
	}

	public BiomeAbstract getBiome(int x, int z) {
		return Biomes.getBiomeByID(biomeIDs[(z + 1) * WIDTH_EXTENDED + x + 1]);
	}

	public float getHeight(int x, int z) {
		return heights[(z + EXTENSION) * (WIDTH_EXTENDED + 1) + (x + EXTENSION)];
	}

	public void setBiomes(byte[] ids) {
		if (ids.length != WIDTH_EXTENDED * WIDTH_EXTENDED) {
			throw new Error("Array for biomes has invalid len!");
		}
		this.biomeIDs = ids;
	}

	public void setHeights(float[] array) {
		if (array.length != (WIDTH_EXTENDED + 1) * (WIDTH_EXTENDED + 1)) {
			throw new Error("Array for heights has invalid len!");
		}
		this.heights = array;
	}

	public void update() {

	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		
		Region r = (Region) obj;
		
		return r.x == this.x && r.z == this.z;
	}
}

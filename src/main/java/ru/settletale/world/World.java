package ru.settletale.world;

import java.util.Random;

import ru.settletale.world.region.Region;
import ru.settletale.world.region.RegionManagerAbstract;

public class World {
	Random random;
	public long seed;
	public RegionManagerAbstract regionManager;
	public WorldUpdateThread updateThread;

	public World(RegionManagerAbstract rm) {
		regionManager = rm;
		updateThread = new WorldUpdateThread(this);
		random = new Random();
		seed = random.nextLong();
	}
	
	public void start() {
		regionManager.start();
	}

	public void update() {
		regionManager.update();
	}

	public boolean regionLoaded(int x, int z) {
		return regionManager.regionLoaded(x, z);
	}

	public Region getRegion(int x, int z) {
		return regionManager.getRegion(x, z);
	}
}

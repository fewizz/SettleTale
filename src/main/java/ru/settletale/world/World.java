package ru.settletale.world;

import ru.settletale.world.region.Region;
import ru.settletale.world.region.RegionManagerAbstract;

public class World {
	public RegionManagerAbstract regionManager;
	public WorldUpdateThread updateThread;

	public World(RegionManagerAbstract rm) {
		regionManager = rm;
		updateThread = new WorldUpdateThread(this);
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

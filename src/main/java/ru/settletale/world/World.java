package ru.settletale.world;

public class World {
	public RegionManager regionManager;
	public WorldUpdateThread updateThread;
	
	public World() {
		regionManager = new RegionManager();
		updateThread = new WorldUpdateThread(this);
	}
	
	public World(RegionManager rm) {
		regionManager = rm;
	}
	
	public void update() {
		regionManager.update();
	}
}

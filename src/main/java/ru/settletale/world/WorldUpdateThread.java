package ru.settletale.world;

import ru.settletale.util.TickTimer;

public class WorldUpdateThread extends Thread {
	final World world;
	final int countOfUpdatesPerSec;
	public static final int DEFAULT_COUNT_OF_UPDATES_PER_TICK = 20;
	
	public WorldUpdateThread(World world) {
		this.world = world;
		this.countOfUpdatesPerSec = DEFAULT_COUNT_OF_UPDATES_PER_TICK;
	}
	
	public WorldUpdateThread(World world, int countOfUpdatesPerSec) {
		this.world = world;
		this.countOfUpdatesPerSec = countOfUpdatesPerSec;
	}
	
	@Override
	public void run() {
		TickTimer timer = new TickTimer(countOfUpdatesPerSec);
		
		for(;;) {
			timer.start();
			
			world.update();
			
			timer.waitTimer();
		}
	}
}

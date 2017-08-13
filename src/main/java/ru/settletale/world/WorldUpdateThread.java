package ru.settletale.world;

import ru.settletale.util.TickTimer;

public class WorldUpdateThread extends Thread {
	final World world;
	final int countOfUpdatesPerSec;
	public static final int DEFAULT_COUNT_OF_UPDATES_PER_TICK = 20;
	public int ticks = 0;
	public TickTimer timer;
	
	public WorldUpdateThread(World world) {
		this.world = world;
		this.countOfUpdatesPerSec = DEFAULT_COUNT_OF_UPDATES_PER_TICK;
		this.timer = new TickTimer(DEFAULT_COUNT_OF_UPDATES_PER_TICK);
	}
	
	public WorldUpdateThread(World world, int countOfUpdatesPerSec) {
		this.world = world;
		this.countOfUpdatesPerSec = countOfUpdatesPerSec;
	}
	
	@Override
	public void run() {
		world.start();
		
		for(;;) {
			timer.start();
			
			world.update();
			
			timer.waitAndRestart();
			
			ticks++;
		}
	}
}

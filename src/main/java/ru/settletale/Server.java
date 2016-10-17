package ru.settletale;

import ru.settletale.world.World;
import ru.settletale.world.WorldUpdateThread;

public class Server {
	public World world;
	static WorldUpdateThread worldThread;
	
	public Server(World world) {
		this.world = world;
		worldThread = new WorldUpdateThread(world);
	}
	
	public void start() {
		worldThread.start();
	}
}

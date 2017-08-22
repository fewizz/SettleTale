package ru.settletale.world;

import java.util.Random;

import ru.settletale.client.KeyListener;
import ru.settletale.util.ThreadWithTasks;
import ru.settletale.util.TickTimer;
import ru.settletale.client.Client;
import ru.settletale.world.region.Chunk;
import ru.settletale.world.region.ChunkManager;
import ru.settletale.world.region.ChunkManagerOnePlayer;

public class World extends ThreadWithTasks {
	public final Random random;
	public final long seed;
	public final ChunkManager chunkManager;
	public final TickTimer tickTimer;

	public World() {
		super("World");
		chunkManager = new ChunkManagerOnePlayer(this);
		random = new Random();
		seed = random.nextLong();
		tickTimer = new TickTimer(20);
	}
	
	@Override
	public void run() {
		chunkManager.start();
		
		for(;;) {
			tickTimer.start();
			update();
			tickTimer.waitAndRestart();
		}
	}

	public void update() {
		KeyListener.updateForCurrentThread();
		Client.player.update();
		chunkManager.update();
	}

	public boolean regionLoaded(int x, int z) {
		return chunkManager.isChunkLoaded(x, z);
	}

	public Chunk getRegion(int x, int z) {
		return chunkManager.getChunk(x, z);
	}
}

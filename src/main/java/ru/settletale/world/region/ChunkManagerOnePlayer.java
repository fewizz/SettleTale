package ru.settletale.world.region;

import java.util.Iterator;

import ru.settletale.client.Client;
import ru.settletale.util.MathUtils;
import ru.settletale.util.TickTimer;
import ru.settletale.world.World;

public class ChunkManagerOnePlayer extends ChunkManager {
	World world;
	protected RegionGenerator regionGenerator;
	public static final int CHUNK_LOAD_RADIUS = 15;

	public ChunkManagerOnePlayer(World world) {
		super();
		this.world = world;
		regionGenerator = new RegionGenerator(world);
	}

	@Override
	public void start() {
		regionGenerator.start();
	}

	@Override
	public void update() {
		int regX = MathUtils.floor(Client.player.position.x / Chunk.WIDTH_F);
		int regZ = MathUtils.floor(Client.player.position.z / Chunk.WIDTH_F);

		chunks.forEach((long key, Chunk obj) -> obj.active = false);

		TickTimer worldTimer = world.tickTimer;

		for (int x = -CHUNK_LOAD_RADIUS + regX; x <= CHUNK_LOAD_RADIUS + regX; x++) {
			for (int z = -CHUNK_LOAD_RADIUS + regZ; z <= CHUNK_LOAD_RADIUS + regZ; z++) {

				float w = x - regX;
				float l = z - regZ;

				float hyp2 = (w * w) + (l * l);

				if (hyp2 >= CHUNK_LOAD_RADIUS * CHUNK_LOAD_RADIUS) {
					continue;
				}

				Chunk chunk = getChunk(x, z);

				if (chunk == null) {
					if (System.nanoTime() - worldTimer.startTimeNano > worldTimer.tickDurationNano - (worldTimer.tickDurationNano / 10)) {
						continue;
					}
					chunk = readOrGenerateChunk(x, z);

					this.chunks.put(chunk.coordClamped, chunk);

					for (IChunkManagerListener listener : listeners) {
						listener.onChunkAdded(chunk);
					}
				}
				chunk.active = true;
			}
		}

		for (Iterator<Chunk> it = chunks.values().iterator(); it.hasNext();) {
			Chunk region = it.next();
			if (!region.active) {

				for (IChunkManagerListener listener : listeners) {
					listener.onChunkRemoved(region);
				}

				it.remove();
				region.decreaseThreadUsage();
			}
		}

		chunks.forEach((long key, Chunk region) -> region.update());
	}

	private Chunk readOrGenerateChunk(int x, int z) {
		Chunk region = null;

		if (region == null) {
			region = generateChunk(x, z);
		}

		return region;
	}

	public Chunk generateChunk(int x, int z) {
		return regionGenerator.generateChunk(x, z);
	}
}

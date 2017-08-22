package ru.settletale.world.region;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.util.MathUtils;

public abstract class ChunkManager {
	public final HashLongObjMap<Chunk> chunks;
	public final List<IChunkManagerListener> listeners;

	public ChunkManager() {
		chunks = HashLongObjMaps.newMutableMap();
		listeners = new CopyOnWriteArrayList<>();
	}

	public void start() {
	}

	abstract public void update();

	public boolean isChunkLoaded(int x, int z) {
		return chunks.containsKey(MathUtils.clampLong(x, z));
	}

	public Chunk getChunk(int x, int z) {
		return chunks.get(MathUtils.clampLong(x, z));
	}

	public float getHeight(float x, float z) {
		int x2 = MathUtils.floor(MathUtils.fract(x / Chunk.WIDTH_F) * (Chunk.WIDTH_F * 2F));
		int z2 = MathUtils.floor(MathUtils.fract(z / Chunk.WIDTH_F) * (Chunk.WIDTH_F * 2F));

		/*
		 * if (x2 < 0) { x2 += 32; }
		 * 
		 * if (z2 < 0) { z2 += 32;d }
		 */

		Chunk r = getChunk(MathUtils.floor(x / Chunk.WIDTH_F), MathUtils.floor(z / Chunk.WIDTH_F));

		if (r == null) {
			System.out.println(0);
			return 0;
		}

		return r.getHeight(x2, z2);
	}
}

package ru.settletale.world.region;

public interface IChunkManagerListener {
	void onChunkAdded(Chunk r);
	void onChunkRemoved(Chunk r);
}

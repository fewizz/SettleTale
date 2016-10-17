package ru.settletale.tile.data;

import java.nio.ByteBuffer;

public class DataContainerTerrain extends TileDataContainer {
	public ByteBuffer buffer;

	public DataContainerTerrain() {
		buffer = ByteBuffer.allocateDirect(9 * 4);
	}
	
}

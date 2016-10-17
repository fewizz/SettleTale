package ru.settletale.tile;

import ru.settletale.client.render.tile.ITileRenderer;
import ru.settletale.tile.data.TileDataContainer;

public abstract class Tile {
	public byte id = -1;
	
	public TileDataContainer newDataContainer() {
		return new TileDataContainer();
	}
	
	public ITileRenderer newRenderer() {
		return null;
	}
}

package ru.settletale.tile;

import ru.settletale.client.render.tile.ITileRenderer;
import ru.settletale.client.render.tile.TileRendererTerrain;
import ru.settletale.tile.data.TileDataContainer;
import ru.settletale.tile.data.DataContainerTerrain;

public class TileTerrain extends Tile {
	@Override
	public TileDataContainer newDataContainer() {
		return new DataContainerTerrain();
	}
	
	@Override
	public ITileRenderer newRenderer() {
		return new TileRendererTerrain();
	}
}

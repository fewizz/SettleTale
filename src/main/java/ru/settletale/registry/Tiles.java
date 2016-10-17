package ru.settletale.registry;

import ru.settletale.Game;
import ru.settletale.client.registry.TileRenderers;
import ru.settletale.tile.Tile;
import ru.settletale.tile.TileAir;
import ru.settletale.tile.TileGrass;
import ru.settletale.util.Side;

public class Tiles {
	public static final Tile[] tiles = new Tile[256];
	private static int lastID = 0;
	public static Tile grass;
	public static Tile air;
	
	public static void register() {
		registerTile(air = new TileAir());
		registerTile(grass = new TileGrass());
	}
	
	private static void registerTile(Tile t) {
		tiles[lastID] = t;
		
		if(Game.getSide() == Side.CLIENT) {
			TileRenderers.renderers[lastID] = t.newRenderer();
		}
		
		t.id = (byte) lastID;
		lastID++;
	}
}

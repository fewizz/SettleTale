package ru.settletale.world;

public class RegionData {
	/*private final byte[] ids;
	private final TileDataContainer[] containers;
	
	public RegionData() {
		ids = new byte[16 * 16];
		containers = new TileDataContainer[16 * 16];
	}*/
	
	/*public void set(int x, int z, Tile tile) {
		this.set(x, z, tile.id);
	}
	
	public void set(int x, int z, int id) {
		ids[index(x, z)] = (byte) id;
	}
	
	public Tile get(int x, int z) {
		int id = ids[index(x, z)];
		return Tiles.tiles[id];
	}
	
	public static int index(int x, int z) {
		return x + (z * 16);
	}
	
	public void setContainer(int x, int z, TileDataContainer c) {
		containers[index(x, z)] = c;
	}
	
	public TileDataContainer getContainer(int x, int z) {
		return containers[index(x, z)];
	}*/
}

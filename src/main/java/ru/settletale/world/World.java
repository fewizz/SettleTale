package ru.settletale.world;

public class World {
	public RegionManager regionManager;
	
	public World() {
		regionManager = new RegionManager();
	}
	
	public World(RegionManager rm) {
		regionManager = rm;
	}
	
	public void update() {
		regionManager.update();
	}
	
	/*public Tile getTile(int x, int z) {
		int cx = SSMath.floor2((float)x / 16F);
		int cz = SSMath.floor2((float)z / 16F);
		
		if(!regionManager.regionLoaded(cx, cz)) {
			return Tiles.air;
		}
		
		return regionManager.getRegion(cx, cz).chunkData.get(x & 0xF, z & 0xF);
	}
	
	public TileDataContainer getTileData(int x, int z) {
		int cx = SSMath.floor2((float)x / 16F);
		int cz = SSMath.floor2((float)z / 16F);
		
		if(!regionManager.regionLoaded(cx, cz)) {
			return null;
		}
		
		return regionManager.getRegion(cx, cz).chunkData.getContainer(x & 0xF, z & 0xF);
	}*/
}

package ru.settletale.world;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.client.Camera;
import ru.settletale.util.IRegionManageristener;
import ru.settletale.util.SSMath;

public class RegionManager {
	protected RegionGenerator regionGenerator;
	public static final int REGIONLOAD_RADIUS = 10;
	public final HashLongObjMap<Region> regions;
	public final List<IRegionManageristener> listeners;
	
	public RegionManager() {
		regions = HashLongObjMaps.newMutableMap();
		regionGenerator = new RegionGenerator();
		listeners = new CopyOnWriteArrayList<>();
	}
	
	public void update() {
		int regX = SSMath.floor(Camera.x / 16F);
		int regZ = SSMath.floor(Camera.z / 16F);
		
		for(Region ch : regions.values()) {
			ch.active = false;
		}
		
		for(int x = -REGIONLOAD_RADIUS + regX; x <= REGIONLOAD_RADIUS + regX; x++) {
			for(int z = -REGIONLOAD_RADIUS + regZ; z <= REGIONLOAD_RADIUS + regZ; z++) {
				float w = x - regX;
				float l = z - regZ;
				
				float hyp = (float) Math.sqrt((w * w) + (l * l));
				
				if(hyp >= REGIONLOAD_RADIUS) {
					continue;
				}
				
				Region regionT = getRegion(x, z);
				final Region region = regionT == null ? readOrGenerateRegion(x, z) : regionT;
				
				if(regionT == null) {
					this.regions.put(region.coord, region);
					for(IRegionManageristener listener : listeners) {
						listener.onAdded(region);
					}
				}
				region.active = true;
			}
		}
		
		for(Iterator<Region> it = regions.values().iterator(); it.hasNext();) {
			Region region = it.next();
			if(!region.active) {
				for(IRegionManageristener listener : listeners) {
					listener.onRemoved(region);
				}
				it.remove();
			}
		}
		
		for(Region chunk : regions.values()) {
			chunk.update();
		}
	}
	
	public boolean regionLoaded(int x, int z) {
		return getRegion(x, z) != null;
	}
	
	public Region getRegion(int x, int z) {
		for(IRegionManageristener listener : listeners) {
			Region r = listener.get(x, z);
			if(r != null) {
				return r;
			}
		}
		return regions.get(SSMath.clamp(x, z));
	}
	
	private Region readOrGenerateRegion(int x, int z) {
		Region region = null;
		
		if(region == null) {
			region = generateRegion(x, z);
		}
		
		return region;
	}
	
	public Region generateRegion(int x, int z) {
		return regionGenerator.generateRegion(x, z);
	}
}

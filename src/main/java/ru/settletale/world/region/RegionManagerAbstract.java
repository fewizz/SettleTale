package ru.settletale.world.region;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.util.MathUtils;

public abstract class RegionManagerAbstract {
	public final HashLongObjMap<Region> regions;
	public final List<IRegionManagerListener> listeners;
	
	public RegionManagerAbstract() {
		regions = HashLongObjMaps.newMutableMap();
		listeners = new CopyOnWriteArrayList<>();
	}
	
	public void start() {}
	
	abstract public void update();
	
	public boolean regionLoaded(int x, int z) {
		return regions.containsKey(MathUtils.clamp(x, z));
	}
	
	public Region getRegion(int x, int z) {
		return regions.get(MathUtils.clamp(x, z));
	}
	
	public float getHeight(float x, float z) {
		int x2 = MathUtils.floor(x * 2);
		int z2 = MathUtils.floor(z * 2);
		
		x2 = x2 % 32;
		z2 = z2 % 32;
		
		if(x2 < 0) {
			x2 += 32;
		}
		
		if(z2 < 0) {
			z2 += 32;
		}
		
		Region r = getRegion(MathUtils.floor(x / 16F), MathUtils.floor(z / 16F));
		
		if(r == null) {
			return -1;
		}
		
		return r.getHeight(x2 + 1, z2 + 1);
	}
}

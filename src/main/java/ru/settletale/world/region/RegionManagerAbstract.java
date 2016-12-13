package ru.settletale.world.region;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import ru.settletale.util.IRegionManageristener;
import ru.settletale.util.SSMath;

public abstract class RegionManagerAbstract {
	public final HashLongObjMap<Region> regions;
	public final List<IRegionManageristener> listeners;
	
	public RegionManagerAbstract() {
		regions = HashLongObjMaps.newMutableMap();
		listeners = new CopyOnWriteArrayList<>();
	}
	
	public void start() {}
	
	abstract public void update();
	
	public boolean regionLoaded(int x, int z) {
		return regions.containsKey(SSMath.clamp(x, z));
	}
	
	public Region getRegion(int x, int z) {
		return regions.get(SSMath.clamp(x, z));
	}
}

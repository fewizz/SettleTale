package ru.settletale.world.region;

import java.util.ArrayDeque;
import java.util.Queue;

public class RegionCache {
	static Queue<Region> cache;
	
	static {
		cache = new ArrayDeque<>();
	}
	
	public static Region getOrCreateNewRegion(int x, int z) {
		Region r = null;
		
		while(!cache.isEmpty()) {
			r = cache.poll();
			
			if(r.threads == 0) {
				break;
			}
			
			r = null;
		}
		
		if(r == null) {
			r = new Region(x, z);
		}
		else {
			r.initInfo(x, z);
		}
		
		return r;
	}
	
	public static void returnRegion(Region r) {
		cache.add(r);
	}
}

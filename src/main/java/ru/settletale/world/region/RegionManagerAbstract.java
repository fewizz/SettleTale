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

	public void start() {
	}

	abstract public void update();

	public boolean regionLoaded(int x, int z) {
		return regions.containsKey(MathUtils.clamp(x, z));
	}

	public Region getRegion(int x, int z) {
		return regions.get(MathUtils.clamp(x, z));
	}

	public float getHeight(float x, float z) {
		int x2 = MathUtils.floor(MathUtils.fract(x / 16F) * 32F);
		int z2 = MathUtils.floor(MathUtils.fract(z / 16F) * 32F);

		/*if (x2 < 0) {
			x2 += 32;
		}

		if (z2 < 0) {
			z2 += 32;d
		}*/

		Region r = getRegion(MathUtils.floor(x / 16F), MathUtils.floor(z / 16F));

		if (r == null) {
			System.out.println(0);
			return 0;
		}

		return r.getHeight(x2, z2);
	}
}

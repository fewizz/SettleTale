package ru.settletale.world.region;

import java.util.Iterator;

import ru.settletale.client.Camera;
import ru.settletale.util.MathUtils;

public class RegionManagerOnePlayer extends RegionManagerAbstract {
	protected RegionGenerator regionGenerator;
	public static final int REGION_LOAD_RADIUS = 20;

	public RegionManagerOnePlayer() {
		super();
		regionGenerator = new RegionGenerator();
	}

	@Override
	public void start() {
		regionGenerator.start();
	}
	
	@Override
	public void update() {
		int regX = MathUtils.floor(Camera.position.x / 16F);
		int regZ = MathUtils.floor(Camera.position.z / 16F);
		
		regions.forEach((long key, Region obj) -> obj.active = false);

		for (int x = -REGION_LOAD_RADIUS + regX; x <= REGION_LOAD_RADIUS + regX; x++) {
			for (int z = -REGION_LOAD_RADIUS + regZ; z <= REGION_LOAD_RADIUS + regZ; z++) {
				float w = x - regX;
				float l = z - regZ;

				float hyp = (float) Math.sqrt((w * w) + (l * l));

				if (hyp >= REGION_LOAD_RADIUS) {
					continue;
				}

				Region region = getRegion(x, z);

				if (region == null) {
					region = readOrGenerateRegion(x, z);
					
					this.regions.put(region.coord, region);
					region.threads++;
					
					for (IRegionManagerListener listener : listeners) {
						listener.onRegionAdded(region);
					}
				}
				region.active = true;
			}
		}

		for (Iterator<Region> it = regions.values().iterator(); it.hasNext();) {
			Region region = it.next();
			if (!region.active) {
				it.remove();
				region.threads--;
				
				for (IRegionManagerListener listener : listeners) {
					listener.onRegionRemoved(region);
				}
				
				RegionCache.returnRegion(region);
			}
		}

		regions.forEach((long key, Region region) -> region.update());
	}

	private Region readOrGenerateRegion(int x, int z) {
		Region region = null;

		if (region == null) {
			region = generateRegion(x, z);
		}

		return region;
	}

	public Region generateRegion(int x, int z) {
		return regionGenerator.generateRegion(x, z);
	}
}

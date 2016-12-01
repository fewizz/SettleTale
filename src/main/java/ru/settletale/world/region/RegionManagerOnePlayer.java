package ru.settletale.world.region;

import java.util.Iterator;

import ru.settletale.client.Camera;
import ru.settletale.util.IRegionManageristener;
import ru.settletale.util.SSMath;

public class RegionManagerOnePlayer extends RegionManagerAbstract {
	protected RegionGenerator regionGenerator;
	public static final int REGION_LOAD_RADIUS = 20;

	public RegionManagerOnePlayer() {
		super();
		regionGenerator = new RegionGenerator();
	}

	@Override
	public void update() {
		int regX = SSMath.floor2(Camera.x / 16F);
		int regZ = SSMath.floor2(Camera.z / 16F);

		for (Region ch : regions.values()) {
			ch.active = false;
		}

		for (int x = -REGION_LOAD_RADIUS + regX; x <= REGION_LOAD_RADIUS + regX; x++) {
			for (int z = -REGION_LOAD_RADIUS + regZ; z <= REGION_LOAD_RADIUS + regZ; z++) {
				float w = x - regX;
				float l = z - regZ;

				float hyp = (float) Math.sqrt((w * w) + (l * l));

				if (hyp >= REGION_LOAD_RADIUS) {
					continue;
				}

				Region regionT = getRegion(x, z);
				final Region region = regionT == null ? readOrGenerateRegion(x, z) : regionT;

				if (regionT == null) {
					this.regions.put(region.coord, region);
					for (IRegionManageristener listener : listeners) {
						listener.onAdded(region);
					}
				}
				region.active = true;
			}
		}

		for (Iterator<Region> it = regions.values().iterator(); it.hasNext();) {
			Region region = it.next();
			if (!region.active) {
				for (IRegionManageristener listener : listeners) {
					listener.onRemoved(region);
				}
				it.remove();
			}
		}

		for (Region chunk : regions.values()) {
			chunk.update();
		}
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

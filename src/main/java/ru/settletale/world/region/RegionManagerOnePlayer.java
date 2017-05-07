package ru.settletale.world.region;

import java.util.Iterator;

import ru.settletale.Game;
import ru.settletale.client.PlatformClient;
import ru.settletale.util.MathUtils;
import ru.settletale.util.TickTimer;

public class RegionManagerOnePlayer extends RegionManagerAbstract {
	protected RegionGenerator regionGenerator;
	public static final int REGION_LOAD_RADIUS = 6;

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
		int regX = MathUtils.floor(PlatformClient.player.position.x / Region.WIDTH_F);
		int regZ = MathUtils.floor(PlatformClient.player.position.z / Region.WIDTH_F);

		regions.forEach((long key, Region obj) -> obj.active = false);

		TickTimer worldTimer = Game.getWorld().updateThread.timer;

		for (int x = -REGION_LOAD_RADIUS + regX; x <= REGION_LOAD_RADIUS + regX; x++) {
			for (int z = -REGION_LOAD_RADIUS + regZ; z <= REGION_LOAD_RADIUS + regZ; z++) {

				float w = x - regX;
				float l = z - regZ;

				float hyp2 = (w * w) + (l * l);

				if (hyp2 >= REGION_LOAD_RADIUS * REGION_LOAD_RADIUS) {
					continue;
				}

				Region region = getRegion(x, z);

				if (region == null) {
					if (System.nanoTime() - worldTimer.startTimeNano > worldTimer.waitTimeNano - (worldTimer.waitTimeNano / 10)) {
						continue;
					}
					region = readOrGenerateRegion(x, z);

					this.regions.put(region.coordClamped, region);

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

				for (IRegionManagerListener listener : listeners) {
					listener.onRegionRemoved(region);
				}

				it.remove();
				region.decreaseThreadUsage();
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

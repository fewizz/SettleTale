package ru.settletale.util;

import ru.settletale.world.region.Region;

public interface IRegionManageristener {
	void onRegionAdded(Region r);
	void onRegionRemoved(Region r);
}

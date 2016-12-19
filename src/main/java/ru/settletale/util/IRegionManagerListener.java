package ru.settletale.util;

import ru.settletale.world.region.Region;

public interface IRegionManagerListener {
	void onRegionAdded(Region r);
	void onRegionRemoved(Region r);
}

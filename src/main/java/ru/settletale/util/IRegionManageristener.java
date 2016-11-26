package ru.settletale.util;

import ru.settletale.world.region.Region;

public interface IRegionManageristener {
	void onAdded(Region r);
	void onRemoved(Region r);
}

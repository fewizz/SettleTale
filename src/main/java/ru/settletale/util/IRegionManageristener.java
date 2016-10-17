package ru.settletale.util;

import ru.settletale.world.Region;

public interface IRegionManageristener {
	void onAdded(Region r);
	void onRemoved(Region r);
	Region get(int x, int z);
}

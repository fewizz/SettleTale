package ru.settletale.world.region;

public interface IRegionManagerListener {
	void onRegionAdded(Region r);
	void onRegionRemoved(Region r);
}

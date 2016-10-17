package ru.settletale.server;

import ru.settletale.world.RegionManager;
import ru.settletale.world.World;

public class WorldServer extends World {

	public WorldServer() {
		super(new RegionManager());
	}
	
}

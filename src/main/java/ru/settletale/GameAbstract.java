package ru.settletale;

import ru.settletale.util.Side;
import ru.settletale.world.World;

public abstract class GameAbstract {
	protected World world;

	public abstract void start();

	public abstract Side getSide();

	public World getWorld() {
		return world;
	}
}

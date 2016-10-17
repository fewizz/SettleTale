package ru.settletale;

import ru.settletale.util.Side;
import ru.settletale.world.World;

public interface IPlatform {
	void start();
	
	Side getSide();
	World getWorld();
}

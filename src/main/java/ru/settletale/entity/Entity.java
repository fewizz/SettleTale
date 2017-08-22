package ru.settletale.entity;

import ru.settletale.util.Vector3dp;
import ru.settletale.world.World;

public abstract class Entity {
	World world;
	
	public Entity(World w) {
		this.world = w;
	}
	public Vector3dp position = new Vector3dp(0, 100, 0);
	public float rotationX = 0;
	public float rotationY = 0;
	
	public abstract void update();
}

package ru.settletale.phys;

import java.util.ArrayList;
import java.util.List;

public abstract class PhysWorld {
	List<CollidableObject> objects = new ArrayList<>();
	
	public void tickPhysics() {
		
	}
	
	public abstract float getGravity();
}

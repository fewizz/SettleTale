package ru.settletale.client;

import ru.settletale.PlatformAbstract;
import ru.settletale.client.render.GLThread;
import ru.settletale.client.render.world.WorldRenderer;
import ru.settletale.client.resource.ResourceManager;
import ru.settletale.entity.EntityPlayer;
import ru.settletale.util.Side;
import ru.settletale.util.TickTimer;
import ru.settletale.world.World;
import ru.settletale.world.region.RegionManagerOnePlayer;

public class PlatformClient extends PlatformAbstract {
	private static final GLThread GL_THREAD = new GLThread();
	public static EntityPlayer player;
	
	@Override
	public Side getSide() {
		return Side.CLIENT;
	}
	
	@Override
	public void start() {	
		GL_THREAD.start(); /** Starting rendering **/
		
		ResourceManager.loadResources();
		
		/** Creating world **/
		player = new EntityPlayer();
		world = new World(new RegionManagerOnePlayer());
		world.regionManager.listeners.add(WorldRenderer.INSTANCE);
		world.updateThread.start();
		/********************/
		
		clientUpdateLoop();
	}
	
	void clientUpdateLoop() {
		TickTimer timer = new TickTimer(100);
		
		for(;;) {
			timer.start();
			
			timer.waitTimer();
		}
	}
}

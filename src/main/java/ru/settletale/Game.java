package ru.settletale;

import ru.settletale.client.PlatformClient;
import ru.settletale.registry.Registry;
import ru.settletale.server.PlatformServer;
import ru.settletale.util.Side;
import ru.settletale.world.World;

public class Game {
	public static PlatformAbstract platform;
	
	public static void main(String[] args) {
		if(args != null && args.length == 1 && args[0].equals("--server")) {
			platform = new PlatformServer();
		}
		else {
			platform = new PlatformClient();
		}
		Registry.init();
		
		platform.start();
	}
	
	public static World getWorld() {
		return platform.getWorld();
	}
	
	public static Side getSide() {
		return platform.getSide();
	}
}

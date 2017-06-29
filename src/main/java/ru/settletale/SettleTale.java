package ru.settletale;

import ru.settletale.client.Client;
import ru.settletale.registry.Registry;
import ru.settletale.server.GameServer;
import ru.settletale.util.Side;
import ru.settletale.world.World;

public class SettleTale {
	public static GameAbstract platform;
	
	public static void main(String... args) {
		if(args != null && args.length == 1 && args[0].equals("--server")) {
			platform = new GameServer();
		}
		else {
			platform = new Client();
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

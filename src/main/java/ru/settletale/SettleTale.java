package ru.settletale;

import ru.settletale.client.Client;
import ru.settletale.server.Server;

public class SettleTale {
	
	public static void main(String... args) {
		if(args != null && args.length == 1 && args[0].equals("--server")) {
			new Server().start(null);
		}
		else {
			new Client().start();
		}
	}
}

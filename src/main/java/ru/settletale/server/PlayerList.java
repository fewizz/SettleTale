package ru.settletale.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerList {
	private static List<Player> playersUnloginned;
	private static List<Player> players;
	
	static void init() {
		playersUnloginned = new CopyOnWriteArrayList<>();
		players = new CopyOnWriteArrayList<>();
	}
	
	public static void tick() {
		for(Player player : playersUnloginned) {
			loginPlayer(player);
		}
	}
	
	public static void addUnloginnedPlayer(Player player) {
		playersUnloginned.add(player);
	}
	
	public static void loginPlayer(Player player) {
		boolean isInList = playersUnloginned.contains(player);
		playersUnloginned.remove(player);
		
		if(!isInList) {
			try {
				throw new Exception("Player is not in to login list.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("New player loginned: " + player.channel);
		
		players.add(player);
	}
}
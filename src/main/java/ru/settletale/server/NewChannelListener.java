package ru.settletale.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class NewChannelListener extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		Player player = new Player();
		player.channel = ch;
		
		System.out.println(Thread.currentThread().getName());
		
		PlayerList.addUnloginnedPlayer(player);
	}

}

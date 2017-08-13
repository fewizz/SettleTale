package ru.settletale.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.settletale.GameAbstract;
import ru.settletale.util.Side;
import ru.settletale.world.World;
import ru.settletale.world.region.RegionManagerOnePlayer;

public class GameServer extends GameAbstract {
	ServerBootstrap bootstrap;

	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	@Override
	public void start() {
		bootstrap = new ServerBootstrap();
		bootstrap.group(new NioEventLoopGroup(1));
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(new NewChannelListener());
		world = new World(new RegionManagerOnePlayer());
		world.updateThread.start();
		
		try {
			Channel ch = bootstrap.bind(25575).sync().channel();
			System.out.println("Server started");
			
			
			
			ch.closeFuture().sync();
			
			System.out.println("Server end");
			bootstrap.config().childGroup().shutdownGracefully();
		} catch (InterruptedException e) {e.printStackTrace();}
	}

}

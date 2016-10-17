package ru.settletale.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import ru.settletale.IPlatform;
import ru.settletale.Server;
import ru.settletale.util.Side;
import ru.settletale.world.World;

public class PlatformServer implements IPlatform {
	ServerBootstrap boot;
	Server server;

	@Override
	public void start() {
		boot = new ServerBootstrap();
		boot.group(new NioEventLoopGroup(1));
		boot.channel(NioServerSocketChannel.class);
		boot.childHandler(new ChannelListener());
		server.start();
		
		try {
			Channel ch = boot.bind(25575).sync().channel();
			System.out.println("Server started");
			
			
			
			ch.closeFuture().sync();
			System.out.println("Server end");
			boot.config().childGroup().shutdownGracefully();
		} catch (InterruptedException e) {e.printStackTrace();}
	}

	@Override
	public Side getSide() {
		return Side.SERVER;
	}

	@Override
	public World getWorld() {
		return null;
	}

}

package ru.settletale.server;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import ru.settletale.util.ThreadTasks;
import ru.settletale.world.World;

public class Server {
	public final World world;
	ServerBootstrap bootstrap;
	ThreadTasks tasks;
	
	public Server() {
		tasks = new ThreadTasks();
		world = new World();
	}
	
	public void bootstrap(ServerBootstrap boot) {
		this.bootstrap = boot;
	}
	
	public void start(InetSocketAddress addrss) {
		world.start();
		
		try {
			bootstrap.bind(addrss).await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

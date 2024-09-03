package com.frontier.shishya.distributed.receiving.guava;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.shishya.ShishyaServer;

final class ServerLoop implements Runnable {
	
	private final ShishyaServer server;
	
	ServerLoop(ShishyaServer server) {
		ObjectValidator.raiseIfNull(server);
		this.server = server;
	}
	
	@Override public void run() {
		this.server.doServerLoop();
	}
}
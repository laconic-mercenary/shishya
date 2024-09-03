/**
 * 
 */
package com.frontier.shishya.distributed.receiving;

import java.net.InetAddress;


/**
 * @author mlcs05
 */
public interface DataServerManager {
	long addServer(int bindPort, InetAddress bindAddress);
	void removeServer(long serverId);
	void closeServers();
}

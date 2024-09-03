/**
 * 
 */
package com.frontier.shishya.server.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.frontier.shishya.server.Server;
import com.frontier.shishya.server.ServerFactory;
import com.frontier.shishya.server.impl.udp.UdpServer;
import com.frontier.shishya.server.impl.udp.UdpServerProperties;

/**
 * @author mlcs05
 *
 */
public class UdpServerFactory implements ServerFactory {

	@Override
	public Server create(Map<String, String> serverConfiguration) {
		int bindPort = parseBindPort(serverConfiguration);
		InetAddress bindAddress = parseAddress(serverConfiguration);
		int bufferSize = parseBufferSize(serverConfiguration);
		return new UdpServer(
				bindPort, 
				bindAddress, 
				bufferSize);
	}

	@Override
	public Class<? extends Server> getServerImplementation() {
		return UdpServer.class;
	}

	@Override
	public List<String> getRequiredConfigurationEntries() {
		return Arrays.asList(UdpServerProperties.getAllProperties());
	}

	private static int parseBindPort(Map<String, String> config) {
		return Integer.parseUnsignedInt(
			config.get(UdpServerProperties.LISTEN_PORT)
		);
	}
	
	private static InetAddress parseAddress(Map<String, String> config) {
		String strAddr = config.get(UdpServerProperties.BIND_ADDRESS);
		InetAddress addr = null;
		try { addr = InetAddress.getByName(strAddr); }
		catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		return addr;
	}
	
	private static int parseBufferSize(Map<String, String> config) {
		return Integer.parseUnsignedInt(
			config.get(UdpServerProperties.RECEIVE_BUFFER_SIZE)
		);
	}
}

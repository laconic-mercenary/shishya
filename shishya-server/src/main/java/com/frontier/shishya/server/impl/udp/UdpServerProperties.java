/**
 * 
 */
package com.frontier.shishya.server.impl.udp;

import java.net.InetAddress;

import com.frontier.lib.validation.NumberValidation;
import com.frontier.lib.validation.ObjectValidator;
import com.frontier.shishya.configuration.PropertyServerConfiguration;
import com.frontier.shishya.configuration.ServerConfiguration;
import com.frontier.shishya.server.impl.UdpServerFactory;

/**
 * @author mlcs05
 *
 */
public abstract class UdpServerProperties {

	public static final String BIND_ADDRESS = "UDP-BIND-ADDRESS";
	
	public static final String LISTEN_PORT = "UDP-LISTEN-PORT";
	
	public static final String RECEIVE_BUFFER_SIZE = "UDP-RX-BUFFER-SIZE";
	
	public static String[] getAllProperties() {
		return new String[] { 
			BIND_ADDRESS,
			LISTEN_PORT,
			RECEIVE_BUFFER_SIZE
		};
	}
	
	public static ServerConfiguration createUdpConfig(int port, InetAddress bindAddress, int bufferSize) {
		ObjectValidator.raiseIfNull(bindAddress);
		NumberValidation.raiseIfLessThan(port, 0L);
		NumberValidation.raiseIfLessThan(bufferSize, 0L);
		PropertyServerConfiguration config = new PropertyServerConfiguration(UdpServerFactory.class);
		config.addEntry(BIND_ADDRESS, bindAddress.getHostAddress());
		config.addEntry(LISTEN_PORT, String.valueOf(port));
		config.addEntry(RECEIVE_BUFFER_SIZE, String.valueOf(bufferSize));
		return config;
	}
}

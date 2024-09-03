/**
 * 
 */
package com.frontier.shishya.configuration.impl;

import java.util.Arrays;
import java.util.List;

import com.frontier.shishya.server.ServerFactory;
import com.frontier.shishya.server.impl.UdpServerFactory;

/**
 * @author mlcs05
 *
 */
final class ServerFactoryRegistry {

	public static List<ServerFactory> getEntries() {
		return Arrays.asList(new UdpServerFactory());
	}
	
}

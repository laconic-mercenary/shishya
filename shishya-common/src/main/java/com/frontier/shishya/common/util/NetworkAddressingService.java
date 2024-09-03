/**
 * 
 */
package com.frontier.shishya.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author mlcs05
 *
 */
public final class NetworkAddressingService {

	public Map<NetworkInterface, List<InetAddress>> getAllAddresses() throws SocketException {
		Map<NetworkInterface, List<InetAddress>> result = new HashMap<>();
		Enumeration<NetworkInterface> netis = NetworkInterface.getNetworkInterfaces();
		while(netis.hasMoreElements()) {
			NetworkInterface neti = netis.nextElement();
			Enumeration<InetAddress> addrs = neti.getInetAddresses();
			List<InetAddress> addresses = new LinkedList<>();
			while (addrs.hasMoreElements()) {
				InetAddress address = addrs.nextElement();
				addresses.add(address);
			}
			result.put(neti, addresses);
		}
		return result;
	}
}

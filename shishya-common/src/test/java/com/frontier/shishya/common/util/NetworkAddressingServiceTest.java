/**
 * 
 */
package com.frontier.shishya.common.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author mlcs05
 *
 */
public class NetworkAddressingServiceTest {

	@Test
	@Ignore
	public void test() throws SocketException {
		NetworkAddressingService nas = new NetworkAddressingService();
		Map<NetworkInterface, List<InetAddress>> map = nas.getAllAddresses();
		for (NetworkInterface neti : map.keySet()) {
			List<InetAddress> addrs = map.get(neti);
			for (InetAddress addr : addrs) {
				System.out.println(String.format("%s:%s", neti, addr));
				System.out.println(addr instanceof Inet6Address);
			}
		}
	}
}

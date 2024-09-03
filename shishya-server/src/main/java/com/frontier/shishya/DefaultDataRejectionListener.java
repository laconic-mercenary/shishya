/**
 * 
 */
package com.frontier.shishya;

import java.net.InetAddress;

import com.frontier.ports.assembler.catching.DataPacketRejectedListener;

/**
 * @author mlcs05
 *
 */
public final class DefaultDataRejectionListener implements DataPacketRejectedListener {

	@Override
	public void onRejected(byte[] packet, InetAddress sender) {
		StringBuilder msg = new StringBuilder("--- REJECTED DATA ---");
		msg.append(String.format("** packet size = %d", packet.length));
		msg.append(String.format("** sender = %s", sender));
		System.out.println(msg.toString());
	}
}

/**
 * 
 */
package com.frontier.shishya;

import java.net.InetAddress;
import java.util.List;

import com.frontier.ports.assembler.DataPacket;
import com.frontier.ports.assembler.catching.DataPacketCompletionListener;

/**
 * @author mlcs05
 *
 */
public final class DefaultDataReadyListener implements DataPacketCompletionListener {
	
	@Override
	public void onDataPacketsComplete(List<DataPacket> packets, InetAddress sender) {
		StringBuilder msg = new StringBuilder("~~~ RECEIVED DATA ~~~");
		msg.append(String.format("** # of packets = %d", packets.size()));
		msg.append(String.format("** payload size = %d", packets.get(0).dataTotalSize));
		msg.append(String.format("** sender = %s", sender));
		System.out.println(msg.toString());
	}
}

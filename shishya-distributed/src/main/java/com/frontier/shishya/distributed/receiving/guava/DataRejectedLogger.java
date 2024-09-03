package com.frontier.shishya.distributed.receiving.guava;

import java.net.InetAddress;

import com.frontier.ports.assembler.catching.DataPacketRejectedListener;

final class DataRejectedLogger implements DataPacketRejectedListener {
	@Override 
	public void onRejected(byte[] packet, InetAddress sender) {
		StringBuilder msg = new StringBuilder(
			String.format("%d bytes were rejected from sender %s", packet.length, sender)
		);
		EventBusServerManager.LOGGER.warn(msg);
	}
}
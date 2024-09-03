/**
 * 
 */
package com.frontier.shishya.distributed.receiving.guava;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.ports.assembler.DataPacket;

/**
 * @author mlcs05
 *
 */
public final class DataReceivedEvent implements Serializable {

	private static final long serialVersionUID = 201512111845L;

	private final List<DataPacket> dataPackets;
	
	private final InetAddress senderAddress;
	
	public DataReceivedEvent(List<DataPacket> packets, InetAddress sender) {
		ObjectValidator.raiseIfNull(packets);
		ObjectValidator.raiseIfNull(sender);
		this.dataPackets = packets;
		this.senderAddress = sender;
	}
	
	public List<DataPacket> getDataPackets() {
		return dataPackets;
	}
	
	public InetAddress getSenderAddress() {
		return senderAddress;
	}
}

/**
 * 
 */
package com.frontier.shishya.distributed.receiving.guava;

import java.net.InetAddress;
import java.util.List;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.ports.assembler.DataPacket;
import com.frontier.ports.assembler.catching.DataPacketCompletionListener;
import com.google.common.eventbus.EventBus;

/**
 * @author mlcs05
 *
 */
public class DataReceivedEventPoster implements DataPacketCompletionListener {
	
	private final EventBus eventBus;
	
	public DataReceivedEventPoster(EventBus eventBus) {
		ObjectValidator.raiseIfNull(eventBus);
		this.eventBus = eventBus;
	}

	@Override
	public void onDataPacketsComplete(List<DataPacket> packets,	InetAddress sender) {
		this.eventBus.post(
			new DataReceivedEvent(packets, sender)
		);
	}
}

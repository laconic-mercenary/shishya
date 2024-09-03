/**
 * 
 */
package com.frontier.shishya.distributed.receiving.guava.reassembly;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.shishya.distributed.receiving.DataPacketReAssembler;
import com.frontier.shishya.distributed.receiving.guava.DataReceivedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * @author mlcs05
 *
 */
public final class DataPayloadReadyPoster {
	
	private final EventBus eventBus;
	
	private final DataPacketReAssembler assembler;

	public DataPayloadReadyPoster(EventBus eventBus, DataPacketReAssembler assembler) {
		ObjectValidator.raiseIfNull(eventBus);
		ObjectValidator.raiseIfNull(assembler);
		this.eventBus = eventBus;
		this.assembler = assembler;
	}
	
	@Subscribe 
	public void onDataReceivedEvent(DataReceivedEvent event) {
		byte[] data = assembler.reassemble(event.getDataPackets());
		this.eventBus.post(
			new DataPayloadReadyEvent(
				data, 
				event.getSenderAddress(),
				event.getDataPackets().get(0).dataType
			)
		);
	}
}

/**
 * 
 */
package com.frontier.shishya.distributed.sending.guava;

import java.net.InetAddress;

import com.frontier.shishya.common.PortGenerator;
import com.frontier.shishya.distributed.sending.DataDistributor;
import com.frontier.shishya.distributed.sending.DataDistributorFactory;
import com.frontier.shishya.distributed.sending.DataSendFailedListener;
import com.google.common.eventbus.EventBus;

/**
 * @author mlcs05
 *
 */
public class EventBusDataDistributorFactory implements DataDistributorFactory {
	
	private final InetAddress bindAddress;
	
	private final PortGenerator portGenerator;          
	
	private final EventBus eventBus;
	
	private final int packetSize;
	
	private final long sendDelayMillis;
	
	private final DataSendFailedListener sendFailedListener;
	
	public EventBusDataDistributorFactory(
			InetAddress bindAddress,
			PortGenerator portGenerator,
			EventBus eventBus,
			int packetSize,
			long sendDelayMillis,
			DataSendFailedListener sendFailedListener) {
		this.bindAddress = bindAddress;
		this.portGenerator = portGenerator;
		this.eventBus = eventBus;
		this.packetSize = packetSize;
		this.sendFailedListener = sendFailedListener;
		this.sendDelayMillis = sendDelayMillis;
	}
	
	@Override
	public DataDistributor create() {
		return new EventBusDataDistributor(
				eventBus, 
				bindAddress, 
				portGenerator, 
				packetSize,
				sendDelayMillis,
				sendFailedListener);
	}

}

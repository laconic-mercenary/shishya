/**
 * 
 */
package com.frontier.shishya.distributed.sending.guava;

import java.io.Closeable;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import com.frontier.lib.validation.NumberValidation;
import com.frontier.lib.validation.ObjectValidator;
import com.frontier.shishya.client.ShishyaClient;
import com.frontier.shishya.common.PortGenerator;
import com.frontier.shishya.distributed.sending.DataDistributor;
import com.frontier.shishya.distributed.sending.DataSendFailedListener;
import com.frontier.shishya.distributed.sending.DataSender;
import com.google.common.eventbus.EventBus;

/**
 * @author mlcs05
 *
 */
public final class EventBusDataDistributor implements DataDistributor, Closeable {
	
	private static final Logger LOGGER = Logger.getLogger(EventBusDataDistributor.class.getName());
	
	private final EventBus eventBus;
	
	private final InetAddress bindAddress;
	
	private final PortGenerator portGenerator;
	
	private final int packetSize;
	
	private final DataSender dataSender;
	
	public EventBusDataDistributor(
			EventBus eventBus, 
			InetAddress bindAddress, 
			PortGenerator portGenerator,
			int packetSize,
			long sendDelayMillis,
			DataSendFailedListener sendFailedListener) {
		LOGGER.trace("ctor");
		ObjectValidator.raiseIfNull(eventBus);
		ObjectValidator.raiseIfNull(bindAddress);
		ObjectValidator.raiseIfNull(portGenerator);
		ObjectValidator.raiseIfNull(sendFailedListener);
		NumberValidation.raiseIfLessThan(packetSize, 0L);
		NumberValidation.raiseIfLessThan(sendDelayMillis, 0L);
		this.eventBus = eventBus;
		this.bindAddress = bindAddress;
		this.portGenerator = portGenerator;
		this.packetSize = packetSize;
		this.dataSender = new DataSender(packetSize, sendDelayMillis);
		eventBus.register(dataSender);
	}

	@Override
	public void close() {
		LOGGER.trace("close()");
		this.eventBus.unregister(this.dataSender);
		for (ShishyaClient client : this.dataSender.getClientList()) {
			client.close();
		}
		this.dataSender.getClientList().clear();
	}

	public void addSender(int targetPort, InetAddress targetAddress) {
		LOGGER.trace("addSender()");
		int port = portGenerator.nextPort();
		ShishyaClient client = new ShishyaClient(bindAddress, port);
		client.setTarget(targetAddress, targetPort);
		this.dataSender.addClient(client);
		if (LOGGER.isDebugEnabled()) {
			StringBuilder msg = new StringBuilder();
			msg.append("Adding new client: ");
			msg.append(String.format("BIND_ADDR=%s,", bindAddress));
			msg.append(String.format("BIND_PORT=%d,", port));
			msg.append(String.format("TARGET_ADDR=%s,", targetAddress));
			msg.append(String.format("TARGET_ADDR=%d,", targetPort));
			msg.append(String.format("PACKET_SIZE=%d", packetSize));
			LOGGER.debug(msg);
		}
	}
}

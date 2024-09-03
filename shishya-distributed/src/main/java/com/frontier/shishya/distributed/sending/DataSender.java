/**
 * 
 */
package com.frontier.shishya.distributed.sending;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.frontier.shishya.client.ShishyaClient;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;

/**
 * @author mlcs05
 *
 */
public final class DataSender {
	
	private static final Logger LOGGER = Logger.getLogger(DataSender.class.getName());
	
	private final int packetSize;
	
	private final long sendDelay;
	
	private final Lock listLock = new ReentrantLock();
	
	private final List<ShishyaClient> clientList = Lists.newLinkedList();
	
	private Iterator<ShishyaClient> clientIterator = null;
	
	public DataSender(int packetSize, long sendDelayMillis) {
		LOGGER.trace("ctor");
		this.packetSize = packetSize;
		this.sendDelay = sendDelayMillis;
	}
	
	public void addClient(ShishyaClient client) {
		LOGGER.trace("addClient()");
		listLock.lock();
		try {
			clientList.add(client);
			clientIterator = Iterables.cycle(clientList).iterator();
		} finally {
			listLock.unlock();
		}
	}
	
	public List<ShishyaClient> getClientList() {
		return clientList;
	}

	// will catch ANYTHING posting a DataReadyEvent object on the event bus
	// this object is registered in
	@Subscribe 
	public void onDataReadyEvent(DataReadyEvent event) {
		LOGGER.trace("onDataReadyEvent()");
		byte[] data = event.getData();
		byte dataType = event.getDataType();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Sending payload of length " + data.length);
		}
		ShishyaClient client = null;
		listLock.lock();
		try {
			client = clientIterator.next();
		} finally {
			listLock.unlock();
		}
		send(dataType, data, client);
	}
	
	private void send(byte dataType, byte[] data, ShishyaClient shishyaClient) {
		if (shishyaClient != null) {
			try {
				shishyaClient.send(dataType, data, this.packetSize, this.sendDelay);
			} catch (Exception e) {
				LOGGER.warn("Exception trapped when attempting to send data.");
				LOGGER.error(e);
			}
		}
	}
}

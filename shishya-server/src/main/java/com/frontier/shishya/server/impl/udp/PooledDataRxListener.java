/**
 * 
 */
package com.frontier.shishya.server.impl.udp;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.ports.RxDataEvent;
import com.frontier.ports.RxListener;
import com.frontier.ports.assembler.catching.Catcher;

/**
 * @author mlcs05
 *
 */
public class PooledDataRxListener implements RxListener {

	private static final Logger LOGGER = Logger
			.getLogger(PooledDataRxListener.class.getName());

	private final ExecutorService executorService;

	private final Catcher catcher;
	
	public PooledDataRxListener(ExecutorService service, Catcher catcher) {
		ObjectValidator.raiseIfNull(service);
		ObjectValidator.raiseIfNull(catcher);
		this.executorService = service;
		this.catcher = catcher;
	}

	@Override
	public void onReceived(final RxDataEvent data) {
		executorService.execute( new Runnable() {
			@Override
			public void run() {
				try {
					byte[] trueData = Arrays.copyOf(data.data, data.dataLength);
					if (LOGGER.isDebugEnabled()) {
						Thread current = Thread.currentThread();
						LOGGER.debug(String.format(
							"(ASYNC) RX Data: %d, Thread: name=%s id=%d", 
							trueData.length, 
							current.getName(),
							current.getId()
						));
					}
					catcher.receivePacket(trueData, data.senderOfData);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}

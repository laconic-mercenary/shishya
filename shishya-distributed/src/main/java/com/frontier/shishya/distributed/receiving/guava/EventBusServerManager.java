/**
 * 
 */
package com.frontier.shishya.distributed.receiving.guava;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.frontier.lib.validation.NumberValidation;
import com.frontier.lib.validation.ObjectValidator;
import com.frontier.shishya.ShishyaServer;
import com.frontier.shishya.configuration.ServerConfiguration;
import com.frontier.shishya.distributed.receiving.DataPacketReAssembler;
import com.frontier.shishya.distributed.receiving.DataServerManager;
import com.frontier.shishya.distributed.receiving.guava.reassembly.DataPayloadReadyPoster;
import com.frontier.shishya.server.impl.udp.UdpServerProperties;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

/**
 * @author mlcs05
 */
public class EventBusServerManager implements DataServerManager {

	static final Logger LOGGER = Logger
			.getLogger(EventBusServerManager.class.getName());
	
	private static final int RANDOM_CEILING = 788544;
	
	// for posting data received events to
	private final EventBus eventBus;
	
	// for executing server looping threads
	private final ExecutorService serverPool = Executors.newCachedThreadPool();

	private final int bufferSize;
	
	private final Map<Long, ShishyaServer> serverMap = Maps.newHashMap();
	
	private final Lock serverMapLock = new ReentrantLock();
	
	private final DataPayloadReadyPoster poster;
	
	public EventBusServerManager(
			EventBus eventBus, 
			int serverBufferSize,
			boolean autoSubscribeReassembler,
			DataPacketReAssembler assembler) {
		ObjectValidator.raiseIfNull(eventBus);
		ObjectValidator.raiseIfNull(assembler);
		NumberValidation.raiseIfLessThan(serverBufferSize, 0L);
		LOGGER.trace("ctor()");
		this.eventBus = eventBus;
		this.bufferSize = serverBufferSize;
		if (autoSubscribeReassembler) {
			poster = new DataPayloadReadyPoster(eventBus, assembler); 
			eventBus.register(poster);
		} else {
			poster = null;
		}
	}

	@Override
	public long addServer(int bindPort, InetAddress bindAddress) {
		LOGGER.trace("addServer()");
		ObjectValidator.raiseIfNull(bindAddress);
		NumberValidation.raiseIfLessThanOrEqualTo(bindPort, 0L);
		
		ServerConfiguration serverConfig = UdpServerProperties.createUdpConfig(
			bindPort, 
			bindAddress, 
			bufferSize
		);

		ShishyaServer newServer = new ShishyaServer(
			serverConfig,
			new DataReceivedEventPoster(this.eventBus),
			new DataRejectedLogger()
		);

		long id = generateId();
		
		if (LOGGER.isInfoEnabled()) {
			StringBuilder msg = new StringBuilder();
			msg.append(String.format("created Server :: port=%d", bindPort));
			msg.append(String.format(", addr=%s", bindAddress));
			msg.append(String.format(", buffersize=%d", bufferSize));
			msg.append(String.format(", id=%d", id));
			msg.append(". Now entering server loop on thread: " + Thread.currentThread().getName());
			LOGGER.info(msg);
		}
		
		serverMapLock.lock();
		try {
			serverMap.put(id, newServer);
			serverPool.execute(new ServerLoop(newServer));
		} finally {
			serverMapLock.unlock();
		}
				
		return id;
	}

	@Override
	public void removeServer(long serverId) {
		LOGGER.trace("removeServer()");
		serverMapLock.lock();
		try {
			if (serverMap.containsKey(serverId)) {
				LOGGER.info(String.format("Removing server with ID: " + serverId));
				serverMap.remove(serverId).shutdown();
			}
		} finally {
			serverMapLock.unlock();
		}
	}

	@Override
	public void closeServers() {
		LOGGER.trace("clearServers()");
		LOGGER.info(String.format("Shutting down %d servers.", serverMap.size()));
		serverMapLock.lock();
		try {
			for (ShishyaServer server : serverMap.values()) {
				server.shutdown();
			}
			serverMap.clear();
			if (poster != null) {
				eventBus.unregister(poster);
			}
			List<?> runnables = serverPool.shutdownNow();
			if (runnables.size() != 0) {
				LOGGER.warn(runnables.size() + " workers were still in the pool when shutdown occurred.");
			}
		} finally {
			serverMapLock.unlock();
		}
		LOGGER.debug("Shutdown complete.");
	}
	
	private static long generateId() {
		long id = System.currentTimeMillis();
		id += new Random().nextInt(RANDOM_CEILING); // powers of 2 are faster for generation
		return id;
	}

}

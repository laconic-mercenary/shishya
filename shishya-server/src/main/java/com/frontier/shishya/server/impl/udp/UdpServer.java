/**
 * 
 */
package com.frontier.shishya.server.impl.udp;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.ports.assembler.DataPacket;
import com.frontier.ports.assembler.catching.Catcher;
import com.frontier.ports.assembler.catching.DataExpiredListener;
import com.frontier.ports.assembler.catching.DataPacketCompletionListener;
import com.frontier.ports.assembler.catching.DataPacketRejectedListener;
import com.frontier.ports.assembler.impl.DefaultPacketSerializer;
import com.frontier.ports.retry.impl.udp.UdpSocket;
import com.frontier.shishya.common.SystemTimeProvider;
import com.frontier.shishya.server.Server;

/**
 * @author mlcs05
 * @see shishya-distributed
 */
public final class UdpServer extends UdpSocket implements Server {
	
	private static final Logger LOGGER = Logger.getLogger(UdpServer.class.getName());
		
	private static final class PurgeCacheListener implements DataExpiredListener {

		@Override public void onDataExpired(List<DataPacket> packetsBuilt) {
			StringBuilder msg = new StringBuilder();
			msg.append("there was data that was not completely received. ");
			msg.append("this data has been purged from the cache.");
			LOGGER.warn(msg.toString());
		}
	}
	
	private static final int CACHE_MAP_CAPACITY = 300;
	
	private static final long DATA_EXPIREY_SECONDS = 25L;
	
	private static final long DATA_EXPIRY_MILLISECONDS = TimeUnit.SECONDS.toMillis(DATA_EXPIREY_SECONDS);
	
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	private DataPacketCompletionListener packetCompletedListener = null;
	
	private DataPacketRejectedListener packetRejectedListener = null;
		
	public UdpServer( 
			int port,
			InetAddress bindAddress, 
			int receiveBufferSize) {
		// setup socket information
		super(
			new SystemTimeProvider(), 
			port, 
			bindAddress, 
			receiveBufferSize
		);
	}

	@Override
	public void start() {
		LOGGER.trace("prepare()");
		// create the packet catcher
		ObjectValidator.raiseIfNull(packetCompletedListener);
		ObjectValidator.raiseIfNull(packetRejectedListener);
		Catcher catcher = new Catcher(
			new DefaultPacketSerializer(), 
			new SystemTimeProvider(), 
			this.packetCompletedListener, 
			this.packetRejectedListener, 
			new PurgeCacheListener(), 
			CACHE_MAP_CAPACITY, 
			DATA_EXPIRY_MILLISECONDS);
		// establish the catcher as the packet assembler
		super.setRxListener(
			new PooledDataRxListener(executorService, catcher)
		);
		super.prepare();
	}

	@Override
	public boolean isRunning() {
		return super.isFinished() == false;
	}

	@Override
	public void receive() {
		if (super.isPrepared() == false) {
			LOGGER.trace("server is not prepared");
			if (super.isRetryReady() == true) {
				LOGGER.info("reattempting preparation...");
				super.prepare();
			} else {
				LOGGER.trace("server is not ready for retry");
				return;
			}
		}
		LOGGER.trace("receive()");
		super.receive();
	}

	@Override
	public void close() {
		LOGGER.trace("close()");
		super.close();
		executorService.shutdown();
	}

	@Override
	public void setDataReadyListener(DataPacketCompletionListener listener) {
		this.packetCompletedListener = listener;
	}

	@Override
	public void setDataRejectedListener(DataPacketRejectedListener listener) {
		this.packetRejectedListener = listener;
	}
}

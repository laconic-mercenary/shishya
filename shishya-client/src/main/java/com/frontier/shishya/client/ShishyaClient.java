/**
 * 
 */
package com.frontier.shishya.client;

import java.net.InetAddress;

import com.frontier.ports.assembler.DataPacketizer;
import com.frontier.ports.assembler.impl.DefaultPacketSerializer;
import com.frontier.ports.assembler.pitching.Pitcher;
import com.frontier.ports.retry.impl.udp.UdpSocket;
import com.frontier.ports.util.DataFragmentor;
import com.frontier.shishya.common.SystemTimeProvider;

/**
 * @author mlcs05
 */
public final class ShishyaClient implements AutoCloseable {
	
	private static final long NO_DELAY = 0L;
		
	private final Pitcher pitcher;
	
	private final UdpSocket socket;
		
	public ShishyaClient(InetAddress bindAddress, int bindPort) {
		socket = new UdpSocket(
			new SystemTimeProvider(), 
			bindPort, 
			bindAddress
		);
		socket.prepare();
		pitcher = new Pitcher(
			new DataPacketizer(
				new DataFragmentor()
			), 
			new DefaultPacketSerializer(), 
			socket
		);
	}
	
	public void setTarget(InetAddress address, int port) {
		socket.setSendPort(port);
		socket.setSendAddress(address);
	}
	
	public void send(byte[] data, int dataChunkSize) throws Exception {
		send(data, dataChunkSize, NO_DELAY);
	}
	
	public void send(byte[] data, int dataChunkSize, long delayMillis) throws Exception {
		pitcher.send(data, dataChunkSize, delayMillis);
	}

	public void send(byte dataType, byte[] data, int dataChunkSize, long delayMillis) throws Exception {
		pitcher.send(dataType, data, dataChunkSize, delayMillis);
	}
	
	@Override
	public void close() {
		socket.close();
	}
	
}

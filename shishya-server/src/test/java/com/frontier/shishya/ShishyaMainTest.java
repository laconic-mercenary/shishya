/**
 * 
 */
package com.frontier.shishya;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.frontier.ports.TimeProvider;
import com.frontier.ports.assembler.DataPacketizer;
import com.frontier.ports.assembler.impl.DefaultPacketSerializer;
import com.frontier.ports.assembler.pitching.DataSignatureGenerator;
import com.frontier.ports.assembler.pitching.Pitcher;
import com.frontier.ports.retry.impl.udp.UdpSocket;
import com.frontier.ports.util.DataFragmentor;

/**
 * @author mlcs05
 *
 */
public class ShishyaMainTest {

	private static final class SysTime implements TimeProvider {

		@Override
		public long currentTimestampMilliseconds() {
			return System.currentTimeMillis();
		}
	}
	
	@Test
	public void test() throws Exception {
		UdpSocket txSocket = new UdpSocket(new SysTime(), 8912,
				InetAddress.getLoopbackAddress());
		txSocket.setSendAddress(InetAddress.getLoopbackAddress());
		txSocket.setSendPort(9111);
		txSocket.prepare();
		Pitcher pitcher = new Pitcher(
				new DataPacketizer(new DataFragmentor()),
				new DefaultPacketSerializer(), 
				txSocket); // UUID generation takes a while, not recommended for PROD
		final int chunkSize = 500;
		try {
			pitcher.send(genData(), chunkSize);
			pitcher.send(genData(), chunkSize);
			pitcher.send(genData(), chunkSize);
			pitcher.send(genData(), chunkSize);
			pitcher.send(genData(), chunkSize);
			pitcher.send(genData(), chunkSize);
			pitcher.send(genData(), chunkSize);
			pitcher.send(genData(), chunkSize);
		} finally {
			txSocket.close();
		}
	}

	@Test
	public void test2() throws Exception {
		UdpSocket txSocket = new UdpSocket(new SysTime(), 8912,
				InetAddress.getLoopbackAddress());
		txSocket.setSendAddress(InetAddress.getLoopbackAddress());
		txSocket.setSendPort(9111);
		txSocket.prepare();
		Pitcher pitcher = new Pitcher(
				new DataPacketizer(new DataFragmentor()),
				new DefaultPacketSerializer(), 
				txSocket);
		final int chunkSize = 5;
		try {
			pitcher.send("asdfqwerfdasasdffdasasdffdas".getBytes(), chunkSize);
		} finally {
			txSocket.close();
		}
	}
	
	private static byte[] genData() {
		int bufSize = Math.abs(new Random().nextInt()) % (Short.MAX_VALUE * 2);
		byte[] data = new byte[bufSize];
		Arrays.fill(data, (byte) 4);
		return data;
	}
}

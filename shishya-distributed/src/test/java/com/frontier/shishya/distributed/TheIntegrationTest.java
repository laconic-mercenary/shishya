/**
 * 
 */
package com.frontier.shishya.distributed;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.frontier.ports.assembler.DataPacket;
import com.frontier.ports.assembler.DataPacketizer;
import com.frontier.ports.util.DataFragmentor;
import com.frontier.shishya.common.PortGenerator;
import com.frontier.shishya.distributed.receiving.DataPacketReAssembler;
import com.frontier.shishya.distributed.receiving.guava.EventBusServerManager;
import com.frontier.shishya.distributed.receiving.guava.reassembly.DataPayloadReadyEvent;
import com.frontier.shishya.distributed.receiving.util.NewArrayDataPacketReAssembler;
import com.frontier.shishya.distributed.sending.DataReadyEvent;
import com.frontier.shishya.distributed.sending.DataSendFailedListener;
import com.frontier.shishya.distributed.sending.guava.EventBusDataDistributor;
import com.frontier.shishya.distributed.testing.FileUpload;
import com.google.common.base.Charsets;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * @author mlcs05
 *
 */
public class TheIntegrationTest {

	private static final class NewPortGenerator implements PortGenerator {

		private int current = 55551;

		@Override
		public int nextPort() {
			return current++;
		}
	}

	private static final class SendFailure implements DataSendFailedListener {

		@Override
		public void onDataSendFailure(byte[] bs, Throwable t) {
			System.out.println("SEND FAILED");
			throw new RuntimeException(t);
		}
	}

	@Test
	public void test_simple_send() {

		ExecutorService executor = Executors.newCachedThreadPool();
		EventBus eventBus = new AsyncEventBus("test-async-bus", executor);
		EventBusServerManager serverManager = new EventBusServerManager(
				eventBus, 45000, true, new NewArrayDataPacketReAssembler());

		serverManager.addServer(56661, InetAddress.getLoopbackAddress());
		serverManager.addServer(56662, InetAddress.getLoopbackAddress());
		serverManager.addServer(56663, InetAddress.getLoopbackAddress());

		EventBusDataDistributor distributor = new EventBusDataDistributor(
				eventBus, InetAddress.getLoopbackAddress(),
				new NewPortGenerator(), 45000, 0L, new SendFailure());

		distributor.addSender(56661, InetAddress.getLoopbackAddress());
		distributor.addSender(56662, InetAddress.getLoopbackAddress());
		distributor.addSender(56663, InetAddress.getLoopbackAddress());

		// curious to see which objects get what messages
		PayloadRx rx = new PayloadRx();
		eventBus.register(rx);

		System.out.println("SENDING...");
		new PayloadTx(eventBus).sendOff();
		System.out.println("SENDING FINISHED");

		try {
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} finally {
			distributor.close();
			serverManager.closeServers();
			eventBus.unregister(rx);
		}
	}
	
	@Test
	public void test1() {

		ExecutorService executor = Executors.newCachedThreadPool();
		EventBus eventBus = new AsyncEventBus("test-async-bus", executor);
		EventBusServerManager serverManager = new EventBusServerManager(
				eventBus, 45000, true, new NewArrayDataPacketReAssembler());

		serverManager.addServer(57661, InetAddress.getLoopbackAddress());
		serverManager.addServer(57662, InetAddress.getLoopbackAddress());
		serverManager.addServer(57663, InetAddress.getLoopbackAddress());

		EventBusDataDistributor distributor = new EventBusDataDistributor(
				eventBus, InetAddress.getLoopbackAddress(),
				new NewPortGenerator(), 45000, 0L, new SendFailure());

		distributor.addSender(57661, InetAddress.getLoopbackAddress());
		distributor.addSender(57662, InetAddress.getLoopbackAddress());
		distributor.addSender(57663, InetAddress.getLoopbackAddress());

		// curious to see which objects get what messages
		PayloadRx rx = new PayloadRx();
		eventBus.register(rx);

		System.out.println("SENDING...");
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		new PayloadTx(eventBus).sendOff();
		System.out.println("SENDING FINISHED");

		try {
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} finally {
			eventBus.unregister(rx);
			distributor.close();
			serverManager.closeServers();
		}
	}
	
	@Test
	@Ignore
	public void test_file_send() throws IOException {
		ConsoleAppender console = new ConsoleAppender(); // create appender
		// configure the appender
		String PATTERN = "%d [%p|%C{1}] %m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.ALL);
		console.activateOptions();
		// add appender to any Logger (here is root)
		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().addAppender(console);

		ExecutorService executor = Executors.newCachedThreadPool();
		EventBus eventBus = new AsyncEventBus("test-async-bus", executor);
		EventBusServerManager serverManager = new EventBusServerManager(
				eventBus, 45000, true, new NewArrayDataPacketReAssembler());

		serverManager.addServer(58661, InetAddress.getLoopbackAddress());
		serverManager.addServer(58662, InetAddress.getLoopbackAddress());
		serverManager.addServer(58663, InetAddress.getLoopbackAddress());

		EventBusDataDistributor distributor = new EventBusDataDistributor(
				eventBus, InetAddress.getLoopbackAddress(),
				new NewPortGenerator(), 35000, 200L, new SendFailure());

		distributor.addSender(58661, InetAddress.getLoopbackAddress());
		distributor.addSender(58662, InetAddress.getLoopbackAddress());
		distributor.addSender(58663, InetAddress.getLoopbackAddress());

		// curious to see which objects get what messages
		PayloadRxFile rx = new PayloadRxFile();
		eventBus.register(rx);

		System.out.println("SENDING...");
		FileUpload fileUpload = new FileUpload(Paths.get("C:\\Users\\mlcs05\\Desktop\\CAM00042.jpg"));
		byte[] rawData = toBytes(fileUpload);
		DataReadyEvent data = new DataReadyEvent(rawData, (byte)0x1);
		eventBus.post(data);
		eventBus.post(data);
		eventBus.post(data);
		eventBus.post(data);
		eventBus.post(data);
		eventBus.post(data);
		System.out.println("SENDING FINISHED");

		try {
			try {
				Thread.sleep(90000L);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} finally {
			distributor.close();
			serverManager.closeServers();
			eventBus.unregister(rx);
		}
	}

	@Test
	public void serialization_test() {
		TxObject to = new TxObject();
		byte[] data = toBytes(to);

		DataPacketizer packeter = new DataPacketizer(new DataFragmentor());
		DataPacket[] packs = packeter.packetize(data, 50000);

		to = toObject(packs[0].packet);
		Assert.assertEquals(packs[0].packet.length, data.length);

		DataPacketReAssembler assembler = new NewArrayDataPacketReAssembler();
		data = assembler.reassemble(Arrays.asList(packs));

		TxObject to2 = toObject(data);
		Assert.assertEquals(to.name, to2.name);
		Assert.assertEquals(to.timeStamp, to2.timeStamp);
	}

	@Test
	public void serialization_test2() {
		TxObject to = new TxObject();
		byte[] data = toBytes(to);

		DataPacketizer packeter = new DataPacketizer(new DataFragmentor());
		DataPacket[] packs = packeter.packetize(data, 2);

		DataPacketReAssembler assembler = new NewArrayDataPacketReAssembler();
		byte[] newdata = assembler.reassemble(Arrays.asList(packs));

		TxObject to2 = toObject(newdata);
		Assert.assertEquals(data.length, newdata.length);
		Assert.assertEquals(to.name, to2.name);
		Assert.assertEquals(to.timeStamp, to2.timeStamp);
	}

	private static final class PayloadRx {
		@Subscribe
		public void onPayload(DataPayloadReadyEvent event) {
			System.out.println("RX: " + event.payload.length);
			TxObject ob = toObject(event.payload);
			System.out.println(ob.name + ", " + ob.timeStamp + ", "
					+ event.payload.length);
		}
	}
	
	private static final class PayloadRxFile {
		@Subscribe
		public void onPayload(DataPayloadReadyEvent event) {
			
			System.out.println("!RX FILE!");
			FileUpload ob = toObject(event.payload);
			System.out.println(ob.getName() + "." + ob.getExtension());
			try {
				Files.write(Paths.get(System.currentTimeMillis() + "_" + ob.getName()), ob.getFileData());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static final class PayloadTx {
		public EventBus bus;

		public PayloadTx(EventBus bus) {
			this.bus = bus;
		}

		public void sendOff() {
			System.out.println("generating data...");
			byte[] rawData = generateData();
			DataReadyEvent data = new DataReadyEvent(rawData, (byte)0x1);
			System.out.println("generating data...");
			bus.post(data);
		}

		private static byte[] generateData() {
			TxObject ob = new TxObject();
			byte[] data = toBytes(ob);
			System.out.println("Generated: " + ob.name + ", " + ob.timeStamp
					+ ", " + data.length);
			return data;
		}
	}

	private static final class TxObject implements Serializable {

		private static final Charset CODING = Charsets.UTF_16;

		private String name;

		private long timeStamp;

		public TxObject() {
			timeStamp = System.currentTimeMillis();
			StringBuilder msg = new StringBuilder();
			for (int i = 0; i < new Random().nextInt(54); i++) {
				msg.append(i);
			}
			name = "FRED-" + msg.toString();
			name = new String(name.getBytes(CODING), CODING);
		}
	}

	private static byte[] toBytes(Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(object);
			oos.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return baos.toByteArray();
	}

	private static <T> T toObject(byte[] data) {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		T tx = null;
		try (ObjectInputStream ois = new ObjectInputStream(bais)) {
			tx = (T) ois.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return tx;
	}
}

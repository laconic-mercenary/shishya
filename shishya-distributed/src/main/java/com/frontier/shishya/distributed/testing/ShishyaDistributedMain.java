/**
 * 
 */
package com.frontier.shishya.distributed.testing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.frontier.shishya.common.PortGenerator;
import com.frontier.shishya.common.util.SerializationService;
import com.frontier.shishya.distributed.receiving.guava.EventBusServerManager;
import com.frontier.shishya.distributed.receiving.guava.reassembly.DataPayloadReadyEvent;
import com.frontier.shishya.distributed.receiving.util.NewArrayDataPacketReAssembler;
import com.frontier.shishya.distributed.sending.DataDistributor;
import com.frontier.shishya.distributed.sending.DataReadyEvent;
import com.frontier.shishya.distributed.sending.DataSendFailedListener;
import com.frontier.shishya.distributed.sending.guava.EventBusDataDistributor;
import com.google.common.base.Charsets;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * @author mlcs05
 *
 */
public final class ShishyaDistributedMain {

	private static final String ARG_BINDPORTS = "bindPorts";
	private static final String ARG_BINDADDR = "bindAddress";
	private static final String ARG_BUFFERSIZE = "bufferSize";
	private static final String ARG_CLIENTFLAG = "CLIENT";
	private static final String ARG_SERVERFLAG = "SERVER";
	private static final String ARG_TARGETPORT = "targetPort";
	private static final String ARG_TARGETADDR = "targetAddress";
	private static final String ARG_SENDFILE = "file";

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		print("Starting...");
		CommandLine cmdLine = buildCommandLine(args);
		checkRequredArgs(cmdLine);
		int[] ports = parseList(cmdLine.getOptionValue(ARG_BINDPORTS));
		InetAddress addr = parseAddress(cmdLine.getOptionValue(ARG_BINDADDR));
		int buffer = Integer.parseInt(cmdLine.getOptionValue(ARG_BUFFERSIZE));
		if (cmdLine.hasOption(ARG_SERVERFLAG)) {
			print("Doing Server...");
			doServer(ports, addr, buffer);
		} else {
			if (cmdLine.hasOption(ARG_CLIENTFLAG)) {
				print("Doing Client...");
				int[] targetPorts = parseList(cmdLine
						.getOptionValue(ARG_TARGETPORT));
				InetAddress targetAddress = parseAddress(cmdLine
						.getOptionValue(ARG_TARGETADDR));
				if (cmdLine.hasOption(ARG_SENDFILE)) {
					String file = cmdLine.getOptionValue(ARG_SENDFILE);
					doClientFile(ports, addr, buffer, targetPorts, targetAddress, Paths.get(file));
				} else {
					doClient(ports, addr, buffer, targetPorts, targetAddress);
				}
			}
		}
	}

	private static CommandLine buildCommandLine(String... args)
			throws ParseException {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption(ARG_BINDPORTS, true,
				"List of comma-separated port numbers to bind.");
		options.addOption(ARG_BINDADDR, true, "Local address to bind to.");
		options.addOption(ARG_BUFFERSIZE, true, "Buffer or packet send size.");

		options.addOption(ARG_CLIENTFLAG, false, "Run in client mode.");
		options.addOption(ARG_TARGETPORT, true,
				"List of comma-seperated ports to send to.");
		options.addOption(ARG_TARGETADDR, true, "Target address to send to.");
		options.addOption(ARG_SENDFILE, true, "Path of file to upload.");

		options.addOption(ARG_SERVERFLAG, false, "Run in server mode.");
		

		CommandLine cmdLine = parser.parse(options, args);
		return cmdLine;
	}

	private static void checkRequredArgs(CommandLine cmd) {
		if (cmd.hasOption(ARG_CLIENTFLAG)) {
			assertArg(ARG_TARGETPORT, cmd);
			assertArg(ARG_TARGETADDR, cmd);
		} else if (cmd.hasOption(ARG_SERVERFLAG)) {

		} else {
			assertArg(ARG_CLIENTFLAG, cmd);
		}
		assertArg(ARG_BINDADDR, cmd);
		assertArg(ARG_BINDPORTS, cmd);
		assertArg(ARG_BUFFERSIZE, cmd);
	}

	private static void assertArg(String arg, CommandLine cmd) {
		if (!cmd.hasOption(arg)) {
			throw new IllegalArgumentException("Expecting Required Argument: "
					+ arg);
		}
	}

	private static int[] parseList(String value) {
		String[] result = value.split(",");
		int[] intresult = new int[result.length];
		for (int i = 0; i < result.length; i++) {
			intresult[i] = Integer.parseInt(result[i].trim());
		}
		return intresult;
	}

	private static InetAddress parseAddress(String value) {
		try {
			return InetAddress.getByName(value);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private static void doServer(int[] bindPorts, InetAddress bindAddress,
			int bufferSize) {
		ExecutorService executor = Executors.newCachedThreadPool();
		EventBus eventBus = new AsyncEventBus(executor);
		EventBusServerManager serverManager = new EventBusServerManager(
				eventBus, bufferSize, true, new NewArrayDataPacketReAssembler());
		try {
			eventBus.register(new Rx());
			for (int port : bindPorts) {
				serverManager.addServer(port, bindAddress);
			}
			print("Server is setup. Awaiting clients...");
			try {
				Thread.sleep(TimeUnit.SECONDS.toMillis(40L));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			serverManager.closeServers();
			executor.shutdownNow();
			
		}
		print("Finished");
	}

	static final class Rx {
		@Subscribe
		public void onData(DataPayloadReadyEvent event) {
			byte[] data = event.payload;
			SerializationService service = new SerializationService();
			try {
				FileUpload file = service.deserialize(data);
				Path path = Paths.get(
					file.getName() + "." + file.getExtension()
				);
				java.nio.file.Files.write(path, file.getFileData());
			} catch (Exception e) {
				// assume it's just a string
				print("RX: " + new String(event.payload, Charsets.UTF_16));
			}
		}
	}

	static final class NextPort implements PortGenerator {

		int i = 0;

		int[] ports;

		public NextPort(int[] ports) {
			this.ports = ports;
		}

		@Override
		public int nextPort() {
			return ports[i++];
		}
	}

	private static final class SendFailed implements DataSendFailedListener {

		@Override
		public void onDataSendFailure(byte[] bs, Throwable t) {
			t.printStackTrace();
		}
	}

	private static void doClientFile(int[] bindPorts, InetAddress bindAddress,
			int packetSize, int[] targetPorts, InetAddress targetAddress, Path file) {
		ExecutorService executor = Executors.newCachedThreadPool(); 
		EventBus eventBus = new AsyncEventBus("client-async-bus", executor);
		DataDistributor distributor = new EventBusDataDistributor(
				eventBus,
				bindAddress, 
				new NextPort(bindPorts), 
				packetSize,
				300L,
				new SendFailed());
		try {
			print("Adding senders...");
			for (int targetPort : targetPorts) {
				distributor.addSender(targetPort, targetAddress);
			}
			
			try {
				print("Sending...");
				FileUpload fileUpload = new FileUpload(file);
				SerializationService service = new SerializationService();
				byte[] raw = service.serialize(fileUpload);
				eventBus.post(new DataReadyEvent(raw, (byte) 0x1));
				print("Pausing for completion...");
				Thread.sleep(TimeUnit.SECONDS.toMillis(5));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
			try {
				distributor.close();
				executor.shutdownNow();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		print("Complete");
	}

	private static void doClient(int[] bindPorts, InetAddress bindAddress,
			int packetSize, int[] targetPorts, InetAddress targetAddress) {
		ExecutorService executor = Executors.newCachedThreadPool(); 
		EventBus eventBus = new AsyncEventBus("client-async-bus", executor);
		DataDistributor distributor = new EventBusDataDistributor(eventBus,
				bindAddress, new NextPort(bindPorts), packetSize, 0L,
				new SendFailed());
		try {
			print("Adding senders...");
			for (int targetPort : targetPorts) {
				distributor.addSender(targetPort, targetAddress);
			}

			print("Ready for user input >>>");
			String input = "";
			Scanner scanner = new Scanner(System.in);
			try {
				while (!input.contains("exit")) {
					input = scanner.nextLine();
					print("Sending: " + input);
					eventBus.post(new DataReadyEvent(input
							.getBytes(Charsets.UTF_16), (byte) 0x1));
				}
			} finally {
				scanner.close();
			}
		} finally {
			print("closing distributor...");
			executor.shutdownNow();
			try {
				distributor.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			print("closed");
		}
		print("Finished");
	}

	private static void print(String message) {
		System.out.println(message);
	}
}

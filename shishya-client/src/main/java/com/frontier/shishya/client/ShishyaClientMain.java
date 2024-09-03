/**
 * 
 */
package com.frontier.shishya.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author mlcs05
 *
 */
public final class ShishyaClientMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		int chunk = 15000;
		InetAddress bindAddress = getBindAddress(args);
		int port = getBindPort(args);
		print("Preparing to send data...");
		try (ShishyaClient client = new ShishyaClient(bindAddress, port)) {
			client.setTarget(getTarget(args), getTargetPort(args));
			Path file = getFilePath(args);
			print(String.format("Serializing file: %s", file));
			byte[] data = serializeFile(file);
			print(String.format("Sending %d bytes of data", data.length));
			client.send(data, chunk);
		}
		print("Closed Client");
	}
	
	private static InetAddress getBindAddress(String... args) throws Exception {
		String address = args[0];
		return InetAddress.getByName(address);
	}
	
	private static int getBindPort(String... args) throws Exception {
		return 7246;
	}
	
	private static Path getFilePath(String... args) throws Exception {
		return Paths.get(args[1]);
	}
	
	private static InetAddress getTarget(String...args) throws Exception {
		return InetAddress.getByName(args[2]);
	}
	
	private static int getTargetPort(String...args) throws Exception {
		return Integer.parseInt(args[3]);
	}
	
	private static byte[] serializeFile(Path file) throws Exception {
		byte[] data = null;
		try (InputStream inputStream = Files.newInputStream(file)) {
			try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream()) {
				int chunk = 0;
				int len = 0;
				do {
					chunk = inputStream.read();
					if (chunk != -1) {
						byteArrayOut.write(chunk);
						len += 1;
					}
				} while (chunk != -1);
				data = new byte[len];
				inputStream.read(data);
			}
		}
		return data;
	}
	
	private static void print(String message) {
		System.out.println(message);
	}
}

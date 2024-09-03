/**
 * 
 */
package com.frontier.shishya.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author mlcs05
 *
 */
public final class SerializationService {

	public byte[] serialize(Object object) throws IOException {
		byte[] result = null;
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
				out.writeObject(object);
			}
			result = baos.toByteArray();
		}
		return result;
	}

	public <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
		T result = null;
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
			try (ObjectInputStream in = new ObjectInputStream(bais)) {
				result = (T)in.readObject();
			}
		}
		return result;
	}
}

/**
 * 
 */
package com.frontier.shishya.distributed.receiving.guava.reassembly;

import java.io.Serializable;
import java.net.InetAddress;

import com.frontier.lib.validation.ObjectValidator;

/**
 * @author mlcs05
 *
 */
public class DataPayloadReadyEvent implements Serializable {

	private static final long serialVersionUID = 201512112145L;
	
	public final byte[] payload;
	
	public final InetAddress sender;
	
	public final byte dataType;

	public DataPayloadReadyEvent(byte[] data, InetAddress sender, byte dataType) {
		ObjectValidator.raiseIfNull(data);
		ObjectValidator.raiseIfNull(sender);
		this.payload = data;
		this.sender = sender;
		this.dataType = dataType;
	}
}

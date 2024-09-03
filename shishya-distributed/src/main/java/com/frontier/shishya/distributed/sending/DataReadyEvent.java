/**
 * 
 */
package com.frontier.shishya.distributed.sending;

import java.io.Serializable;

/**
 * @author mlcs05
 *
 */
public class DataReadyEvent implements Serializable {

	private static final long serialVersionUID = 201512041745L;
	
	private static final byte NO_TYPE = 0x0;

	private final byte[] data;
	
	private final byte dataType;
	
	public DataReadyEvent(byte[] data) {
		this(data, NO_TYPE);
	}
	
	public DataReadyEvent(byte[] data, byte dataType) {
		this.data = data;
		this.dataType = dataType;
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public byte getDataType() {
		return dataType;
	}
}

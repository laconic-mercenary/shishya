/**
 * 
 */
package com.frontier.shishya.distributed.receiving.util;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.ports.assembler.DataPacket;
import com.frontier.shishya.distributed.receiving.DataPacketReAssembler;

/**
 * @author mlcs05
 *
 */
public class NewArrayDataPacketReAssembler implements DataPacketReAssembler {

	private static final Logger LOGGER = Logger
			.getLogger(NewArrayDataPacketReAssembler.class.getName());
	
	private static final byte[] EMPTY_DATA = new byte[0];

	@Override
	public byte[] reassemble(List<DataPacket> packets) {
		LOGGER.trace("reassemble()");
		ObjectValidator.raiseIfNull(packets);
		byte[] data = EMPTY_DATA;
		if (packets.isEmpty()) {
			LOGGER.warn("Packet list was empty, no assembly will be performed.");
		} else {
			long bufferSize = packets.get(0).dataTotalSize;
			if (bufferSize > Integer.MAX_VALUE) {
				LOGGER.error("Buffer size is greater than Integer.MAX: " + bufferSize);
			} else {
				LOGGER.trace("reassembling...");
				data = new byte[(int)bufferSize];
				int startIndex = 0;
				// sort by index
				Collections.sort(packets, new DataPacketIndexComparator());
				// reassemble
				for (DataPacket packet : packets) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("startIndex=" + startIndex);
						LOGGER.trace("packetLength=" + packet.packet.length);
						LOGGER.trace("packetIndex=" + packet.packetIndex);
					}
					emplace(data, startIndex, packet.packet);
					startIndex += packet.packet.length;
				}
				if (LOGGER.isDebugEnabled()) {
					StringBuilder msg = new StringBuilder();
					msg.append("DATA ASSEMBLY REPORT::");
					msg.append(String.format("signature=%s,", packets.get(0).dataSignature));
					msg.append(String.format("packets=%d,", packets.size()));
					msg.append(String.format("data-length=%d", data.length));
					LOGGER.debug(msg);
				}
			}
		}
		return data;
	}
	
	private static void emplace(byte[] data, int start, byte[] chunk) {
		int dataIndex = start;
		for (int i = 0; i != chunk.length; i++) {
			data[dataIndex] = chunk[i];
			++dataIndex;
		}
	}
}

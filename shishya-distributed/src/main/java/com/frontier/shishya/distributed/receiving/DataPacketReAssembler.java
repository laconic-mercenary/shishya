/**
 * 
 */
package com.frontier.shishya.distributed.receiving;

import java.util.List;

import com.frontier.ports.assembler.DataPacket;

/**
 * @author mlcs05
 *
 */
public interface DataPacketReAssembler {
	byte[] reassemble(List<DataPacket> packets);
}

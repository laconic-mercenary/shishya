/**
 * 
 */
package com.frontier.shishya.distributed.receiving.util;

import java.util.Comparator;

import com.frontier.ports.assembler.DataPacket;

/**
 * @author mlcs05
 *
 */
public class DataPacketIndexComparator implements Comparator<DataPacket> {

	@Override
	public int compare(DataPacket dp1, DataPacket dp2) {
		return Short.compare(dp1.packetIndex, dp2.packetIndex);
	}

}

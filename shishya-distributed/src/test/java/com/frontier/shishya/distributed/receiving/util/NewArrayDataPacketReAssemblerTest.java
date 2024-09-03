/**
 * 
 */
package com.frontier.shishya.distributed.receiving.util;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.frontier.ports.assembler.DataPacket;
import com.frontier.shishya.distributed.receiving.DataPacketReAssembler;

/**
 * @author mlcs05
 *
 */
public class NewArrayDataPacketReAssemblerTest {

	@Test
	public void test_2_packets() {
		DataPacketReAssembler assembler = new NewArrayDataPacketReAssembler();
		List<DataPacket> dataPackets = new LinkedList<>();
		DataPacket p1 = new DataPacket();
		p1.packet = new byte[] { 1,2,3 };
		p1.dataTotalSize = 6;
		p1.packetIndex = 0;
		DataPacket p2 = new DataPacket();
		p2.packet = new byte[] { 4,5,6 };
		p2.dataTotalSize = 6;
		p2.packetIndex = 1;
		dataPackets.add(p2);
		dataPackets.add(p1);
		byte[] data = assembler.reassemble(dataPackets);
		Assert.assertEquals(6, data.length);
		Assert.assertEquals(1, data[0]);
		Assert.assertEquals(2, data[1]);
		Assert.assertEquals(3, data[2]);
		Assert.assertEquals(4, data[3]);
		Assert.assertEquals(5, data[4]);
		Assert.assertEquals(6, data[5]);
	}
	
	@Test
	public void test_3_packets_variable() {
		DataPacketReAssembler assembler = new NewArrayDataPacketReAssembler();
		List<DataPacket> dataPackets = new LinkedList<>();
		DataPacket p1 = new DataPacket();
		p1.packet = new byte[] { 1,2,3 };
		p1.dataTotalSize = 6;
		p1.packetIndex = 0;
		DataPacket p2 = new DataPacket();
		p2.packet = new byte[] { 4 };
		p2.dataTotalSize = 6;
		p2.packetIndex = 1;
		DataPacket p3 = new DataPacket();
		p3.packet = new byte[] { 5,6 };
		p3.dataTotalSize = 6;
		p3.packetIndex = 2;
		dataPackets.add(p3);
		dataPackets.add(p2);
		dataPackets.add(p1);
		byte[] data = assembler.reassemble(dataPackets);
		Assert.assertEquals(6, data.length);
		Assert.assertEquals(1, data[0]);
		Assert.assertEquals(2, data[1]);
		Assert.assertEquals(3, data[2]);
		Assert.assertEquals(4, data[3]);
		Assert.assertEquals(5, data[4]);
		Assert.assertEquals(6, data[5]);
	}

	@Test
	public void test_1_packet() {
		DataPacketReAssembler assembler = new NewArrayDataPacketReAssembler();
		List<DataPacket> dataPackets = new LinkedList<>();
		DataPacket p1 = new DataPacket();
		p1.packet = new byte[] { 1,2,3 };
		p1.dataTotalSize = 3;
		p1.packetIndex = 0;
		dataPackets.add(p1);
		byte[] data = assembler.reassemble(dataPackets);
		Assert.assertEquals(3, data.length);
		Assert.assertEquals(1, data[0]);
		Assert.assertEquals(2, data[1]);
		Assert.assertEquals(3, data[2]);
	}
}

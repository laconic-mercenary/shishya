package com.frontier.shishya.server.impl.udp;

import com.frontier.ports.RxDataEvent;
import com.frontier.ports.RxListener;
import com.frontier.ports.assembler.catching.Catcher;

final class DataRxListener implements RxListener {
	
	private final Catcher catcher;
	
	public DataRxListener(Catcher catcher) {
		this.catcher = catcher;
	}

	@Override public void onReceived(RxDataEvent data) {
		try {
			catcher.receivePacket(data.data, data.senderOfData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
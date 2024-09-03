/**
 * 
 */
package com.frontier.shishya.server;

import com.frontier.ports.assembler.catching.DataPacketCompletionListener;
import com.frontier.ports.assembler.catching.DataPacketRejectedListener;


/**
 * @author mlcs05
 *
 */
public interface Server {
	void start();
	boolean isRunning();
	void receive();
	void close();
	void setDataReadyListener(DataPacketCompletionListener listener);
	void setDataRejectedListener(DataPacketRejectedListener listener);
}

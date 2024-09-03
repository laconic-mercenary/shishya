/**
 * 
 */
package com.frontier.shishya.distributed.sending;

import java.io.Closeable;
import java.net.InetAddress;

/**
 * @author mlcs05
 *
 */
public interface DataDistributor extends Closeable {
	void addSender(int targetPort, InetAddress targetAddress);
}

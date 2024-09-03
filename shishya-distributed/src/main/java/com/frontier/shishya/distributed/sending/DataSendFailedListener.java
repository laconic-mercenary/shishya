/**
 * 
 */
package com.frontier.shishya.distributed.sending;

/**
 * @author mlcs05
 *
 */
public interface DataSendFailedListener {
	void onDataSendFailure(byte[] bs, Throwable t);
}

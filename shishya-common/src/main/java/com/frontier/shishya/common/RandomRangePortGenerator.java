/**
 * 
 */
package com.frontier.shishya.common;

import java.util.LinkedList;
import java.util.List;

import com.frontier.lib.RandomGeneration;

/**
 * @author mlcs05
 *
 */
public final class RandomRangePortGenerator implements PortGenerator {
	
	private static final int DEFAULT_GENERATION_ATTEMPTS = 10;
	
	private final int minPortNumber;
	
	private final int maxPortNumber;
	
	private final boolean keepTrackOfPrevious;
	
	private final List<Integer> generated = new LinkedList<>();
	
	public RandomRangePortGenerator(int minPort, int maxPort) {
		this(minPort, maxPort, false);
	}
	
	public RandomRangePortGenerator(int minPort, int maxPort, boolean keepTrack) {
		this.minPortNumber = minPort;
		this.maxPortNumber = maxPort;
		this.keepTrackOfPrevious = keepTrack;
	}

	@Override
	public int nextPort() {
		return nextPort(DEFAULT_GENERATION_ATTEMPTS);
	}
	
	private int nextPort(int attempts) {
		int result = RandomGeneration.random(minPortNumber, maxPortNumber + 1);
		if (this.keepTrackOfPrevious) {
			Integer resultObj = new Integer(result);
			if (generated.contains(resultObj) == false) {
				generated.add(resultObj);
			} else {
				result = nextPort(attempts - 1);
			}
		}
		return result;
	}
}

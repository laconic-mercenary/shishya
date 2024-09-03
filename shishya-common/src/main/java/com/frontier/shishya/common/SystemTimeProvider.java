package com.frontier.shishya.common;

import com.frontier.ports.TimeProvider;

public final class SystemTimeProvider implements TimeProvider {
	
	@Override
	public long currentTimestampMilliseconds() {
		return System.currentTimeMillis();
	}
	
}
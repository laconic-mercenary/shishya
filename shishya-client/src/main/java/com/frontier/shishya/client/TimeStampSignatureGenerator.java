/**
 * 
 */
package com.frontier.shishya.client;

import java.util.Random;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.ports.TimeProvider;
import com.frontier.ports.assembler.pitching.DataSignatureGenerator;

/**
 * @author mlcs05
 *
 */
public class TimeStampSignatureGenerator implements DataSignatureGenerator {
	
	private final TimeProvider timeProvider;
	
	public TimeStampSignatureGenerator(TimeProvider timeProvider) {
		ObjectValidator.raiseIfNull(timeProvider);
		this.timeProvider = timeProvider;
	}

	@Override
	public String generateSignature() {
		return String.format(
			"%s--%d", 
			timeProvider.currentTimestampMilliseconds(),
			new Random().nextInt()
		);
	}

}

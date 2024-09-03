/**
 * 
 */
package com.frontier.shishya.configuration;

import java.util.HashMap;
import java.util.Map;

import com.frontier.shishya.server.ServerFactory;

/**
 * @author mlcs05
 *
 */
public final class PropertyServerConfiguration implements ServerConfiguration {
	
	private final Map<String, String> configurationEntries = new HashMap<>();
	
	private final Class<? extends ServerFactory> classImplementation;
	
	public PropertyServerConfiguration(Class<? extends ServerFactory> serverClassImplementation) {
		this.classImplementation = serverClassImplementation;
	}
	
	public void addEntry(String key, String value) {
		configurationEntries.put(key, value);
	}

	@Override
	public Class<? extends ServerFactory> getImplementationFactory() {
		return classImplementation;
	}

	@Override
	public Map<String, String> getConfigurationEntries() {
		return configurationEntries;
	}

}

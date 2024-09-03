/**
 * 
 */
package com.frontier.shishya.configuration;

import java.util.Map;

import com.frontier.shishya.server.ServerFactory;

/**
 * @author mlcs05
 *
 */
public interface ServerConfiguration {
	Class<? extends ServerFactory> getImplementationFactory();
	Map<String, String> getConfigurationEntries();
}

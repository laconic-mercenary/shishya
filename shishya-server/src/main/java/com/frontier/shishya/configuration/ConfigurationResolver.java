/**
 * 
 */
package com.frontier.shishya.configuration;

/**
 * @author mlcs05
 *
 */
public interface ConfigurationResolver<CONFIGURATION_SOURCE> {
	ServerConfiguration resolveFrom(CONFIGURATION_SOURCE source);
}

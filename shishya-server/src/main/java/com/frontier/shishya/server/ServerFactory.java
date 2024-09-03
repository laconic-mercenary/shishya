/**
 * 
 */
package com.frontier.shishya.server;

import java.util.List;
import java.util.Map;


/**
 * @author mlcs05
 *
 */
public interface ServerFactory {

	Server create(Map<String, String> serverConfiguration);
	
	Class<? extends Server> getServerImplementation();

	List<String> getRequiredConfigurationEntries();
	
}

/**
 * 
 */
package com.frontier.shishya.configuration.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.frontier.shishya.configuration.ConfigurationResolver;
import com.frontier.shishya.configuration.ServerConfiguration;
import com.frontier.shishya.server.ServerFactory;

/**
 * @author mlcs05
 *
 */
public final class PropertiesResolver implements
		ConfigurationResolver<Properties> {
	
	private static final Logger LOGGER = Logger.getLogger(PropertiesResolver.class.getName());
	
	private static final String PROP_FACTORY_IMPLEMENTATION = "server-factory-type";
	
	private static final Map<String, List<String>> PROPERTY_EXPECTATIONS = loadExpectations();

	@Override
	public ServerConfiguration resolveFrom(Properties source) {
		assertEntryPresent(source, PROP_FACTORY_IMPLEMENTATION);
		assertHasRequiredProperties(source);
		return loadServerConfiguration(source);
	}
	
	private static Map<String, List<String>> loadExpectations() {
		Map<String, List<String>> map = new HashMap<>();
		List<ServerFactory> factoryList = ServerFactoryRegistry.getEntries();
		LOGGER.debug(String.format("there are %d registered server factories", factoryList.size()));
		for (ServerFactory factory : factoryList) {
			map.put(
				factory.getClass().getName(), 
				factory.getRequiredConfigurationEntries()
			);
		}
		return map;
	}

	private static void assertEntryPresent(Properties properties, String entry) {
		if (!properties.containsKey(entry)) {
			throw new IllegalArgumentException(String.format(
				"Server configuration properties require missing entry '%s'.", entry
			));
		}
	}
	
	private static void assertHasRequiredProperties(Properties properties) {
		String implementation = properties.getProperty(PROP_FACTORY_IMPLEMENTATION);
		List<String> requiredProperties = PROPERTY_EXPECTATIONS.get(implementation);
		for (String requiredProperty : requiredProperties) {
			assertEntryPresent(properties, requiredProperty);
		}
	}
	
	private static ServerConfiguration loadServerConfiguration(Properties properties) {
		LOGGER.debug("configuration has all the needed entries, creating server config...");
		final Map<String, String> result = new HashMap<>();
		Class<? extends ServerFactory> clazz = null;
		for (Object prop : properties.keySet()) {
			if (PROP_FACTORY_IMPLEMENTATION.equals(prop)) {
				try {
					String factoryClassName = properties.getProperty(PROP_FACTORY_IMPLEMENTATION);
					ServerFactory factory = (ServerFactory) Class.forName(factoryClassName).newInstance();
					clazz = factory.getClass();
				} catch (Exception cnfe) {
					throw new RuntimeException(cnfe);
				}
			} else {
				result.put(prop.toString(), properties.getProperty(prop.toString()));
			}
		}
		final Class<? extends ServerFactory> actualClazz = clazz;
		return new ServerConfiguration() {
			@Override public Class<? extends ServerFactory> getImplementationFactory() {
				return actualClazz;
			}
			
			@Override public Map<String, String> getConfigurationEntries() {
				return result;
			}
		};
	}
}

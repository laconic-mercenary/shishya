/**
 * 
 */
package com.frontier.shishya;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.frontier.ports.assembler.catching.DataPacketCompletionListener;
import com.frontier.ports.assembler.catching.DataPacketRejectedListener;
import com.frontier.shishya.configuration.ConfigurationResolver;
import com.frontier.shishya.configuration.ServerConfiguration;
import com.frontier.shishya.configuration.impl.PropertiesResolver;

/**
 * @author mlcs05
 *
 */
public final class ShishyaMain {
	
	private static final Logger LOGGER = Logger.getLogger(ShishyaMain.class.getName());
	
	private static final String PROP_DATA_COMPLETE_LISTENER = "data-ready-listener-classname";
	
	private static final String PROP_DATA_REJECTED_LISTENER = "data-rejected-listener-classname";

	public static void main(String[] args) {
		LOGGER.info("startup");
		ConfigurationResolver<Properties> configResolver = new PropertiesResolver();
		Properties properties = loadPropertiesFile(args);
		LOGGER.debug("resolving configuration...");
		ServerConfiguration serverConfiguration = configResolver.resolveFrom(properties);
		kickOffServer(serverConfiguration);
		LOGGER.debug("finished");
	}
	
	private static void kickOffServer(ServerConfiguration serverConfiguration) {
		LOGGER.info("starting server...");
		
		DataPacketCompletionListener completionListener = getPacketCompletionListener(serverConfiguration);
		DataPacketRejectedListener rejectionListener = getPacketRejectionListener(serverConfiguration);
		ShishyaServer server = new ShishyaServer(serverConfiguration, completionListener, rejectionListener);
		
		LOGGER.debug("entering server loop...");
		server.doServerLoop();
		
		LOGGER.info("server stopped");
	}
	
	private static DataPacketCompletionListener getPacketCompletionListener(ServerConfiguration config) {
		DataPacketCompletionListener listener = null;
		Map<String, String> map = config.getConfigurationEntries();
		String className = map.get(PROP_DATA_COMPLETE_LISTENER);
		if (className != null) {
			LOGGER.debug("instantiating data ready listener: " + className);
			listener = instantiate(className, DataPacketCompletionListener.class);
		}
		if (listener == null) {
			LOGGER.debug("no data ready listener found, using default console listener.");
			listener = new DefaultDataReadyListener();
		}
		return listener;
	}
	
	private static DataPacketRejectedListener getPacketRejectionListener(ServerConfiguration config) {
		DataPacketRejectedListener listener = null;
		Map<String, String> map = config.getConfigurationEntries();
		String className = map.get(PROP_DATA_REJECTED_LISTENER);
		if (className != null) {
			LOGGER.debug("instantiating data rejection listener: " + className);
			listener = instantiate(className, DataPacketRejectedListener.class);
		}
		if (listener == null) {
			LOGGER.debug("no data rejected listener found, using default console listener.");
			listener = new DefaultDataRejectionListener();
		}
		return listener;
	}
	
	private static Properties loadPropertiesFile(String[] args) {
		LOGGER.debug("loading properies configuration...");
		Properties props = new Properties();
		Path propPath = Paths.get(args[0]);
		LOGGER.debug(propPath);
		try (InputStream fileStream = Files.newInputStream(propPath)) {
			props.load(fileStream);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		return props;
	}
	
	private static <T> T instantiate(String classname, Class<T> clazz) {
		T result = null;
		try {
			result = (T)Class.forName(classname).newInstance();
		} catch (Exception e) {
			LOGGER.warn("Unable to instantiate provided class: " + classname);
		}
		return result;
	}
}

/**
 * 
 */
package com.frontier.shishya;

import org.apache.log4j.Logger;

import com.frontier.lib.validation.ObjectValidator;
import com.frontier.ports.assembler.catching.DataPacketCompletionListener;
import com.frontier.ports.assembler.catching.DataPacketRejectedListener;
import com.frontier.shishya.configuration.ServerConfiguration;
import com.frontier.shishya.server.Server;
import com.frontier.shishya.server.ServerFactory;

/**
 * @author mlcs05
 *
 */
public final class ShishyaServer {
	
	private static final Logger LOGGER = Logger.getLogger(ShishyaServer.class.getName());
	
	private final ServerConfiguration serverProperties;
	
	private final DataPacketCompletionListener dataReadyListener;
	
	private final DataPacketRejectedListener dataRejectedListener;
	
	private volatile boolean shutdown = false;
	
	public ShishyaServer(
			ServerConfiguration serverConfiguration,
			DataPacketCompletionListener dataReadyListener,
			DataPacketRejectedListener dataRejectedListener) {
		LOGGER.trace("ctor()");
		ObjectValidator.raiseIfNull(serverConfiguration);
		ObjectValidator.raiseIfNull(serverConfiguration.getImplementationFactory());
		ObjectValidator.raiseIfNull(serverConfiguration.getConfigurationEntries());
		this.serverProperties = serverConfiguration;
		this.dataReadyListener = dataReadyListener;
		this.dataRejectedListener = dataRejectedListener;
	}
	
	public void doServerLoop() {
		LOGGER.trace("doServerLoop()");
		Server server = instantiateServer(serverProperties);
		LOGGER.info("starting server");
		server.setDataReadyListener(dataReadyListener);
		server.setDataRejectedListener(dataRejectedListener);
		LOGGER.debug("data-ready-listener=" + dataReadyListener);
		LOGGER.debug("data-rejection-listener=" + dataRejectedListener);
		server.start();
		try {
			LOGGER.info("entering into server loop");
			while(server.isRunning() && this.shutdown == false) {
				LOGGER.trace("server.receive()");
				server.receive();
			}
		} finally {
			LOGGER.debug("closing server");
			server.close();
			LOGGER.info("server closed");
		}
	}
	
	public void shutdown() {
		this.shutdown = true;
	}
	
	private static Server instantiateServer(ServerConfiguration properties) {
		Class<? extends ServerFactory> clazz = properties.getImplementationFactory();
		ServerFactory serverFactory = null;
		LOGGER.debug("instantiating server factory: " + clazz);
		try {
			serverFactory = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		LOGGER.debug("creating server instance");
		return serverFactory.create(properties.getConfigurationEntries());
	}
}

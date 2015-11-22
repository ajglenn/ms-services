package com.dialectify.ws;

import java.io.Closeable;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.netflix.blitz4j.LoggingConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.karyon.server.KaryonServer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public abstract class STrackerServer implements Closeable
{

	private static final Logger logger = Logger.getLogger(STrackerServer.class
			.getName());

	private final Server jettyServer;
	private final KaryonServer karyonServer;

	static
	{
		LoggingConfiguration.getInstance().configure();
	}

	public STrackerServer()
	{
		System.setProperty(DynamicPropertyFactory.ENABLE_JMX, "true");

		karyonServer = new KaryonServer();
		karyonServer.initialize();
		jettyServer = new Server();
	}

	public void start(String applicationName)
	{
		ServletHolder sh = new ServletHolder(ServletContainer.class);
		sh.setInitParameter("javax.ws.rs.Application", applicationName);
		sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature",
				"true");
		sh.setInitOrder(1);

		final Server server = new Server(8090);
		ServletContextHandler context = new ServletContextHandler(server, "/",
				ServletContextHandler.SESSIONS);
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		context.addServlet(sh, "/*");

		try
		{
			karyonServer.start();
			server.start();
			server.join();
		}
		catch (Exception exc)
		{
			throw new RuntimeException("Cannot start karyon server ...", exc);
		}
	}

	public void close()
	{
		try
		{
			jettyServer.stop();
			karyonServer.close();
		}
		catch (Exception exc)
		{
			logger.info("Error stopping jetty ..." + exc);
		}

		LoggingConfiguration.getInstance().stop();

	}
}

package com.dialectify.ws;

import java.io.Closeable;
import java.util.EnumSet;
import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.Servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.netflix.blitz4j.LoggingConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.karyon.server.KaryonServer;
import com.netflix.zuul.context.ContextLifecycleFilter;
import com.netflix.zuul.http.ZuulServlet;
import com.dialectify.ws.artifacts.EdgeListener;
import com.dialectify.ws.build.STBuild;

public abstract class STrackerServer implements Closeable
{

	private static final Logger logger = Logger.getLogger(STrackerServer.class
			.getName());

	private Server jettyServer;
	private KaryonServer karyonServer;

	static
	{
		LoggingConfiguration.getInstance().configure();
	}

	public STrackerServer()
	{
		System.setProperty(DynamicPropertyFactory.ENABLE_JMX, "true");
	}

	public void start(STBuild build) throws ClassNotFoundException
	{
		ServletHolder sh = new ServletHolder((Class<? extends Servlet>) Class.forName(build.getServletClassName()));
		sh.setInitOrder(1);
	
		jettyServer = new Server(8089);
		ServletContextHandler context = new ServletContextHandler(jettyServer, "/",
				ServletContextHandler.SESSIONS);
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		context.addServlet(sh, "/*");
		context.addFilter(ContextLifecycleFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		context.addEventListener(new EdgeListener());
	
		try
		{
			karyonServer = new KaryonServer();
			karyonServer.initialize();
			karyonServer.start();
			jettyServer.start();
			jettyServer.join();
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

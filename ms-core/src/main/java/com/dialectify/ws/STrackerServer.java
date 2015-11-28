package com.dialectify.ws;

import java.io.Closeable;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.Holder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.netflix.blitz4j.LoggingConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.karyon.server.KaryonServer;
import com.dialectify.ws.build.ContextClass;
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

	public void start(STBuild build) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		System.setProperty(DynamicPropertyFactory.ENABLE_JMX, build.getEnableJmx());
	
		jettyServer = new Server(build.getListenPort());
		ServletContextHandler context = new ServletContextHandler(jettyServer, "/",
				ServletContextHandler.SESSIONS);
		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		
		loadServletInContext(context, build);
		loadFilterInContext(context, build);
		loadListenerInContext(context, build);
	
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
	
	private <T> T loadClassInstanceByName(String className, Class<T> type) 
	{
		try
		{
			Class<T> clazz = (Class<T>) Class.forName(className);
			return clazz.newInstance();
		}
		catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private void loadServletInContext(ServletContextHandler context, STBuild build)
	{
		if(null == build.getServletClass())
		{
			return;
		}
		
		ServletHolder sh = new ServletHolder(loadClassInstanceByName(build.getServletClass().getName(), Servlet.class));
		sh.setInitOrder(1);
		
		loadParametersToHolder(sh, build.getServletClass());
		
		context.addServlet(sh, "/*");
	}
	
	private void loadFilterInContext(ServletContextHandler context, STBuild build)
	{
		if(null == build.getFilterClass())
		{
			return;
		}
		
		FilterHolder filterHolder = new FilterHolder(loadClassInstanceByName(build.getFilterClass().getName(), Filter.class));
		
		loadParametersToHolder(filterHolder, build.getFilterClass());
		
		context.addFilter(filterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));	
	}
	
	private void loadListenerInContext(ServletContextHandler context, STBuild build)
	{
		if(null == build.getListenerClass())
		{
			return;
		}
		
		context.addEventListener(loadClassInstanceByName(build.getListenerClass().getName(), ServletContextListener.class));
	}
	
	private void loadParametersToHolder(Holder holder, ContextClass contextClass)
	{
		if(null == contextClass.getProperties())
		{
			return;
		}
		
		for(Map.Entry<String,String> entry : contextClass.getProperties().entrySet())
		{
			logger.info(entry.getKey() + " -> " + entry.getValue());
			holder.setInitParameter(entry.getKey(), entry.getValue());
		}
	}
	
}

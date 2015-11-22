package com.dialectify.ws.build;

import javax.servlet.Servlet;

public class STBuild
{
	private String applicationName;
	private String servletClassName;

	public String getApplicationName()
	{
		return applicationName;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}
	
	public void validate()
	{
		if(null == applicationName ||
		   applicationName.length() == 0)
		{
			throw new IllegalArgumentException("Application name missing");
		}
	}

	public String getServletClassName()
	{
		return servletClassName;
	}

	public void setServletClassName(String servletClassName)
	{
		this.servletClassName = servletClassName;
	}
}

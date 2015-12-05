package com.dialectify.ws.build;


public class STBuild
{
    private boolean enableJmx;
    private String applicationName;
    private int listenPort;
    private ContextClass servletClass;
    private ContextClass listenerClass;
    private ContextClass filterClass;

    public String getEnableJmx()
    {
        return enableJmx ? "true" : "false";
    }

    public boolean isEnableJmx()
    {
        return enableJmx;
    }

    public void setEnableJmx(boolean enableJmx)
    {
        this.enableJmx = enableJmx;
    }

    public ContextClass getFilterClass()
    {
        return filterClass;
    }

    public void setFilterClass(ContextClass filterClass)
    {
        this.filterClass = filterClass;
    }

    public ContextClass getListenerClass()
    {
        return listenerClass;
    }

    public void setListenerClass(ContextClass listenerClass)
    {
        this.listenerClass = listenerClass;
    }

    public ContextClass getServletClass()
    {
        return servletClass;
    }

    public void setServletClass(ContextClass servletClass)
    {
        this.servletClass = servletClass;
    }

    public int getListenPort()
    {
        return listenPort;
    }

    public void setListenPort(int listenPort)
    {
        this.listenPort = listenPort;
    }

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
        if (null == applicationName ||
                applicationName.length() == 0)
        {
            throw new IllegalArgumentException("Application name missing");
        }
    }
}

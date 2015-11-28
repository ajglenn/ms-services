/*
 * Copyright 2013 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.dialectify.ws.artifacts;

import static com.netflix.zuul.constants.ZuulConstants.ZUUL_CASSANDRA_ENABLED;
import static com.netflix.zuul.constants.ZuulConstants.ZUUL_FILTER_CUSTOM_PATH;
import static com.netflix.zuul.constants.ZuulConstants.ZUUL_FILTER_POST_PATH;
import static com.netflix.zuul.constants.ZuulConstants.ZUUL_FILTER_PRE_PATH;
import static com.netflix.zuul.constants.ZuulConstants.ZUUL_FILTER_ROUTING_PATH;
import static com.netflix.zuul.constants.ZuulConstants.ZUUL_NIWS_CLIENTLIST;
import static com.netflix.zuul.constants.ZuulConstants.ZUUL_NIWS_DEFAULTCLIENT;
import static com.netflix.zuul.constants.ZuulConstants.ZUUL_RIBBON_NAMESPACE;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.configuration.AbstractConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.client.ClientException;
import com.netflix.client.ClientFactory;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.servo.util.ThreadCpuStats;
import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.ZuulApplicationInfo;
import com.netflix.zuul.constants.ZuulConstants;
import com.netflix.zuul.context.NFRequestContext;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.dependency.ribbon.RibbonConfig;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import com.netflix.zuul.monitoring.CounterFactory;
import com.netflix.zuul.monitoring.MonitoringHelper;
import com.netflix.zuul.monitoring.TracerFactory;
import com.netflix.zuul.plugins.Counter;
import com.netflix.zuul.plugins.MetricPoller;
import com.netflix.zuul.plugins.ServoMonitor;
import com.netflix.zuul.plugins.Tracer;
import com.netflix.zuul.stats.AmazonInfoHolder;
import com.netflix.zuul.stats.monitoring.MonitorRegistry;

public class EdgeListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(EdgeListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("starting server");

        try
        {
	        AmazonInfoHolder.getInfo();
	        initPlugins();
	        initZuul();
	        initNIWS();
	
	        ApplicationInfoManager.getInstance().setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        }
        catch(Exception e)
        {
        	throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("stopping server");
    }
    
    private void initPlugins() {
        MonitorRegistry.getInstance().setPublisher(new ServoMonitor());

        MetricPoller.startPoller();

        TracerFactory.initialize(new Tracer());

        CounterFactory.initialize(new Counter());

        final ThreadCpuStats stats = ThreadCpuStats.getInstance();
        stats.start();
    }

    private void initNIWS() throws ClientException {
        String stack = ConfigurationManager.getDeploymentContext().getDeploymentStack();

        if (stack != null && !stack.trim().isEmpty() && RibbonConfig.isAutodetectingBackendVips()) {
            RibbonConfig.setupDefaultRibbonConfig();
            ZuulApplicationInfo.setApplicationName(RibbonConfig.getApplicationName());
        } else {
            DynamicStringProperty DEFAULT_CLIENT = DynamicPropertyFactory.getInstance().getStringProperty(ZUUL_NIWS_DEFAULTCLIENT, null);
            if (DEFAULT_CLIENT.get() != null) {
                ZuulApplicationInfo.setApplicationName(DEFAULT_CLIENT.get());
            } else {
                ZuulApplicationInfo.setApplicationName(stack);
            }
        }
        String clientPropertyList = DynamicPropertyFactory.getInstance().getStringProperty(ZUUL_NIWS_CLIENTLIST, "").get();
        String[] aClientList = clientPropertyList.split("\\|");
        String namespace = DynamicPropertyFactory.getInstance().getStringProperty(ZUUL_RIBBON_NAMESPACE, "ribbon").get();
        for (String client : aClientList) {
            DefaultClientConfigImpl clientConfig = DefaultClientConfigImpl.getClientConfigWithDefaultValues(client, namespace);
            ClientFactory.registerClientFromProperties(client, clientConfig);
        }
    }

    void initZuul() throws Exception, IllegalAccessException, InstantiationException {

        RequestContext.setContextClass(NFRequestContext.class);

        CounterFactory.initialize(new Counter());
        TracerFactory.initialize(new Tracer());

        final AbstractConfiguration config = ConfigurationManager.getConfigInstance();

        final String preFiltersPath = config.getString(ZUUL_FILTER_PRE_PATH);
        final String postFiltersPath = config.getString(ZUUL_FILTER_POST_PATH);
        final String routingFiltersPath = config.getString(ZUUL_FILTER_ROUTING_PATH);
        final String customPath = config.getString(ZUUL_FILTER_CUSTOM_PATH);

        FilterLoader.getInstance().setCompiler(new GroovyCompiler());
        FilterFileManager.setFilenameFilter(new GroovyFileFilter());
        if (customPath == null) {
            FilterFileManager.init(5, preFiltersPath, postFiltersPath, routingFiltersPath);
        } else {
            FilterFileManager.init(5, preFiltersPath, postFiltersPath, routingFiltersPath, customPath);
        }
    }
}
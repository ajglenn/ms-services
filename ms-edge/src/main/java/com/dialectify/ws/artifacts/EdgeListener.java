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

import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import com.netflix.zuul.monitoring.MonitoringHelper;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(EdgeListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("starting server");

        // mocks monitoring infrastructure as we don't need it for this simple app
        MonitoringHelper.initMocks();

        // initializes groovy filesystem poller
        initGroovyFilterManager();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("stopping server");
    }

    private void initGroovyFilterManager() {
        FilterLoader.getInstance().setCompiler(new GroovyCompiler());

        //String scriptRoot = System.getProperty("zuul.filter.root", "");
        String scriptRoot = "src/main/groovy/filters";
        if (scriptRoot.length() > 0) scriptRoot = scriptRoot + File.separator;
        try {
            FilterFileManager.setFilenameFilter(new GroovyFileFilter());
            FilterFileManager.init(5, scriptRoot + "pre", scriptRoot + "route", scriptRoot + "post");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer.
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import net.jforum.util.log.LoggerHelper;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.stats.Stats;

/**
 * @author Andowson Chang
 */

public class ContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(ContextListener.class);
    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    @Override public void contextInitialized (ServletContextEvent sce) {
        final ServletContext application = sce.getServletContext();
        String appPath = application.getRealPath("");
        if (appPath != null && appPath.endsWith(File.separator)) {
			// On Tomcat, getRealPath ends with a "/", whereas on Jetty, it does not. The next line allows for that.
        	appPath = appPath.substring(0, appPath.lastIndexOf(File.separator));
        }
		LOGGER.info("application root is "+appPath);
        LoggerHelper.checkLoggerInitialization(appPath + "/WEB-INF", appPath + "/WEB-INF/classes");
        ConfigLoader.startSystemglobals(appPath);
        SystemGlobals.setValue("context.path", application.getContextPath());
        final String containerInfo = application.getServerInfo();
		SystemGlobals.setValue("server.info", containerInfo);
		SystemGlobals.setValue("servlet.version", application.getMajorVersion()+"."+application.getMinorVersion());
		// initialize EventBus
		Stats.init();
        LOGGER.info(application.getContextPath() + " initialized in " + containerInfo);
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    @Override public void contextDestroyed (ServletContextEvent sce) {
		// stop EventBus
        Stats.stop();
/*
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            LOGGER.debug("unregister JDBC Driver " + driver.getClass().getName());
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage(), e);
            }
        }
*/
        LOGGER.info(sce.getServletContext().getContextPath() + " destroyed");
    }
}

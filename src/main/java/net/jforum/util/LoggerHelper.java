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
 * Created on 01.11.2013 by Heri
 * The JForum Project
 * http://www.jforum.net
 */

package net.jforum.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * @author Heri
 *
 */
public final class LoggerHelper
{
    /**
     * 
     */
    private LoggerHelper()
    {
        super();
    }

    public static void checkLoggerInitialization( String templateDir, String classpathDir )
    {
        try
        {
            if ( loggerFrameworkFullyConfigured() ) {
                return;
            }

            checkClasspathDir( classpathDir );
            File template = checkTemplate( templateDir );
            File dest = checkDestFile( classpathDir );
            FileUtils.copyFile( template, dest, true );
            DOMConfigurator.configure( dest.toURI().toURL() );
        }
        catch ( Exception e )
        {
            System.err.println( "problems initializing the logger: " + e.getMessage() );
            e.printStackTrace();
        }
    }

    static File checkTemplate( String aTemplateDir ) throws Exception
    {
        File template = new File( aTemplateDir, "log4j_template.xml" );
        if ( !template.exists() )
        {
            // maybe old installation which still has log4j.xml in WEB-INF:
            File firstTemplate = template;
            template = new File( aTemplateDir, "log4j.xml" );
            if ( !template.exists() )
            {
                throw new Exception( "template not found: \"" + firstTemplate + "\"" );
            }
        }
        return template;
    } 

    /**
     * @param classpathDir
     * @throws Exception
     */
    static void checkClasspathDir( String classpathDir )
        throws Exception
        {
        File destDir = new File( classpathDir );
        if ( !destDir.exists() )
        {
            throw new Exception( "classpathDir does not exist: \"" + classpathDir + "\"" );
        }
        if ( !destDir.isDirectory() )
        {
            throw new Exception( "classpathDir is not directory: \"" + classpathDir + "\"" );
        }
        }

    /**
     * @param classpathDir
     * @return
     * @throws IOException
     * @throws Exception
     * @throws MalformedURLException
     */
    private static File checkDestFile( String classpathDir )
        throws IOException, Exception, MalformedURLException
        {
        File dest = new File( classpathDir, "log4j.xml" );
        if ( dest.exists() )
        {
            // check if the file also can be found on the classpath:
            Enumeration<URL> urls = ClassLoader.getSystemResources( "log4j.xml" );
            if ( !urls.hasMoreElements() )
            {
                throw new Exception( "classpathDir is not classpath: \"" + classpathDir + "\"" );
            }
            boolean found = false;
            while ( urls.hasMoreElements() )
            {
                URL url = urls.nextElement();
                if ( url.equals( dest.toURI().toURL() ) )
                {
                    // -> log4j.xml is on the classpath, but must be corrupt after the logger is not correctly initialized
                    FileUtils.copyFile( dest, new File( dest.getName() + ".bak" ), true );
                    found = true;
                    break;
                }
            } // while ( urls )

            if ( !found )
            {
                throw new Exception( "classpathDir is not classpath: \"" + classpathDir + "\"" );
            }

            dest.delete();
        }
        return dest;
        }

    /**
     * Tests if the logger is configured. Returns <code>true</code>
     * if there is at least one appender found.
     * <p>
     * Since this method loads itself the LogManager it is ensured that it executes its built in 
     * auto-configuration (unless not yet done before).
     * <p>
     * @return <code>true</code> if at least one appender is configured.
     */
    static boolean loggerFrameworkFullyConfigured()
    {
        if ( LogManager.getRootLogger().getAllAppenders().hasMoreElements() )
        {
            return true;
        }

        List<Logger> loggers = getCurrentLoggers();
        for ( Logger logger : loggers )
        {
            if ( logger.getAllAppenders().hasMoreElements() )
            {
                // at least one appender found
                return true;
            }
        }

        return false;
    }

    private static List<Logger> getCurrentLoggers()
    {
        List<Logger> result = new ArrayList<Logger>();
        @SuppressWarnings( "rawtypes")
        Enumeration loggers = LogManager.getCurrentLoggers();
        while ( loggers.hasMoreElements() )
        {
            Logger logger = (Logger) loggers.nextElement();
            result.add( logger );
        } // while ( loggers )

        return result;
    }

}

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
 * Created on 30/11/2005 17:07:51
 * The JForum Project
 * http://www.jforum.net
 */

package net.jforum;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import org.apache.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import net.jforum.exceptions.DatabaseException;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

public class HikariPooledConnection extends DBConnection
{
	private static final Logger LOGGER = Logger.getLogger(HikariPooledConnection.class);

	private transient HikariDataSource dataSource;

	/**
	 * @see net.jforum.DBConnection#init()
	 */
	@Override public void init() throws PropertyVetoException
	{
		HikariConfig config = new HikariConfig();
		config.setDriverClassName(SystemGlobals.getValue(ConfigKeys.DATABASE_CONNECTION_DRIVER));
		config.setJdbcUrl(SystemGlobals.getValue(ConfigKeys.DATABASE_CONNECTION_STRING));
		config.setMinimumIdle(SystemGlobals.getIntValue(ConfigKeys.DATABASE_POOL_MIN));		
		config.setMaximumPoolSize(SystemGlobals.getIntValue(ConfigKeys.DATABASE_POOL_MAX));
		// KeepaliveTime: The minimum allowed value is 30000ms (30 seconds)
		config.setKeepaliveTime(Math.max(30000L, 1000L * SystemGlobals.getIntValue(ConfigKeys.DATABASE_PING_DELAY)));

		this.dataSource = new HikariDataSource(config);

		try {
			// Try to validate the connection url
			final Connection conn = this.getConnection();

			if (conn != null) {
				this.releaseConnection(conn);
				this.databaseUp = true;
			}
		} catch (Exception e) {
			this.databaseUp = false;
		}
	}

	/**
	 * @see net.jforum.DBConnection#getConnection()
	 */
	@Override public Connection getConnection()
	{
		try {
			return this.dataSource.getConnection();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}

	/**
	 * @see net.jforum.DBConnection#realReleaseAllConnections()
	 */
	@Override public void realReleaseAllConnections()
	{
		try {
			dataSource.close();
			Thread.sleep(1000);
			this.databaseUp = false;
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
			throw new DatabaseException(e);
		}
	}
}

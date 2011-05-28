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
 * Created on 28/08/2006 23:12:09
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.MailIntegrationDAO;
import net.jforum.entities.MailIntegration;
import net.jforum.exceptions.DatabaseException;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id$
 */
public class GenericMailIntegrationDAO implements MailIntegrationDAO
{
	/**
	 * @see net.jforum.dao.MailIntegrationDAO#add(net.jforum.entities.MailIntegration)
	 */
	public void add(MailIntegration integration)
	{
		PreparedStatement p = null;
		
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("MailIntegration.add"));
			this.prepareForSave(integration, p);
			p.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(p);
		}
	}
	
	/**
	 * @see net.jforum.dao.MailIntegrationDAO#delete(int)
	 */
	public void delete(int forumId)
	{
		PreparedStatement p = null;
		
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("MailIntegration.delete"));
			p.setInt(1, forumId);
			p.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(p);
		}
	}

	/**
	 * @see net.jforum.dao.MailIntegrationDAO#find(int)
	 */
	public MailIntegration find(int forumId)
	{
		MailIntegration m = null;
		
		PreparedStatement p = null;
		ResultSet rs = null;
		
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("MailIntegration.find"));
			p.setInt(1, forumId);
			rs = p.executeQuery();
			
			if (rs.next()) {
				m = this.buildMailIntegration(rs);
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
		
		return m;
	}

	/**
	 * @see net.jforum.dao.MailIntegrationDAO#findAll()
	 */
	public List<MailIntegration> findAll()
	{
		List<MailIntegration> l = new ArrayList<MailIntegration>();
		
		PreparedStatement p = null;
		ResultSet rs = null;
		
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("MailIntegration.findAll"));
			rs = p.executeQuery();
			
			while (rs.next()) {
				l.add(this.buildMailIntegration(rs));
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
		
		return l;
	}

	/**
	 * @see net.jforum.dao.MailIntegrationDAO#update(net.jforum.entities.MailIntegration)
	 */
	public void update(MailIntegration integration)
	{
		PreparedStatement p = null;
		
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("MailIntegration.update"));
			
			this.prepareForSave(integration, p);
			p.setInt(8, integration.getForumId());
			
			p.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(p);
		}
	}
	
	private MailIntegration buildMailIntegration(ResultSet rs) throws SQLException
	{
		MailIntegration mi = new MailIntegration();
		
		mi.setForumId(rs.getInt("forum_id"));
		mi.setForumEmail(rs.getString("forum_email"));
		mi.setPopHost(rs.getString("pop_host"));
		mi.setPopPassword(rs.getString("pop_password"));
		mi.setPopPort(rs.getInt("pop_port"));
		mi.setPopUsername(rs.getString("pop_username"));
		mi.setSsl(rs.getInt("pop_ssl") == 1);
		
		return mi;
	}

	/**
	 * Given a PreparedStatement, fill its values with the data of a MailIntegration instance
	 * @param integration the data to fill the statement
	 * @param p the statement to be filled
	 * @throws SQLException
	 */
	private void prepareForSave(MailIntegration integration, PreparedStatement p) throws SQLException
	{
		p.setInt(1, integration.getForumId());
		p.setString(2, integration.getForumEmail());
		p.setString(3, integration.getPopHost());
		p.setString(4, integration.getPopUsername());
		p.setString(5, integration.getPopPassword());
		p.setInt(6, integration.getPopPort());
		p.setInt(7, integration.isSsl() ? 1 : 0);
	}
}

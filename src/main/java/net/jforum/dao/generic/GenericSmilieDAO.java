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
 * Created on 13/01/2004 / 12:02:54
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
import net.jforum.entities.Smilie;
import net.jforum.exceptions.DatabaseException;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class GenericSmilieDAO extends AutoKeys implements net.jforum.dao.SmilieDAO
{
	/**
	 * @see net.jforum.dao.SmilieDAO#addNew(net.jforum.entities.Smilie)
	 */
	@Override public int addNew(Smilie smilie)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = this.getStatementForAutoKeys("SmiliesModel.addNew");

			pstmt.setString(1, smilie.getCode());
			pstmt.setString(2, smilie.getUrl());
			pstmt.setString(3, smilie.getDiskName());

			this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("SmiliesModel.lastGeneratedSmilieId"));

			return this.executeAutoKeysQuery(pstmt);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.SmilieDAO#delete(int)
	 */
	@Override public void delete(int id)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("SmiliesModel.delete"));
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.SmilieDAO#update(net.jforum.entities.Smilie)
	 */
	@Override public void update(Smilie smilie)
	{
		PreparedStatement pstmt = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("SmiliesModel.update"));
			pstmt.setString(1, smilie.getCode());
			pstmt.setString(2, smilie.getUrl());
			pstmt.setString(3, smilie.getDiskName());
			pstmt.setInt(4, smilie.getId());

			pstmt.executeUpdate();
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(pstmt);
		}
	}

	private Smilie getSmilie(ResultSet rs) throws SQLException
	{
		Smilie s = new Smilie();

		s.setId(rs.getInt("smilie_id"));
		s.setCode(rs.getString("code"));
		s.setUrl(rs.getString("url"));
		s.setDiskName(rs.getString("disk_name"));

		return s;
	}

	/**
	 * @see net.jforum.dao.SmilieDAO#selectAll()
	 */
	@Override public List<Smilie> selectAll()
	{
		List<Smilie> l = new ArrayList<>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("SmiliesModel.selectAll"));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				l.add(this.getSmilie(rs));
			}

			return l;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}

	/**
	 * @see net.jforum.dao.SmilieDAO#selectById(int)
	 */
	@Override public Smilie selectById(int id)
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("SmiliesModel.selectById"));
			pstmt.setInt(1, id);

			Smilie s = new Smilie();

			rs = pstmt.executeQuery();
			if (rs.next()) {
				s = this.getSmilie(rs);
			}

			return s;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, pstmt);
		}
	}
}

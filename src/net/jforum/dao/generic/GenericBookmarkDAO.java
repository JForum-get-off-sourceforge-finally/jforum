/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
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
 * Created on Jan 16, 2005 12:47:31 PM
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
import net.jforum.entities.Bookmark;
import net.jforum.entities.BookmarkType;
import net.jforum.exceptions.DatabaseException;
import net.jforum.exceptions.InvalidBookmarkTypeException;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id: GenericBookmarkDAO.java,v 1.8 2006/08/23 02:13:42 rafaelsteil Exp $
 */
public class GenericBookmarkDAO implements net.jforum.dao.BookmarkDAO
{
	/**
	 * @see net.jforum.dao.BookmarkDAO#add(net.jforum.entities.Bookmark)
	 */
	public void add(Bookmark bookmark)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("BookmarkModel.add"));
			p.setInt(1, bookmark.getUserId());
			p.setInt(2, bookmark.getRelationId());
			p.setInt(3, bookmark.getRelationType());
			p.setInt(4, bookmark.isPublicVisible() ? 1 : 0);
			p.setString(5, bookmark.getTitle());
			p.setString(6, bookmark.getDescription());
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
	 * @see net.jforum.dao.BookmarkDAO#update(net.jforum.entities.Bookmark)
	 */
	public void update(Bookmark bookmark)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("BookmarkModel.update"));
			p.setInt(1, bookmark.isPublicVisible() ? 1 : 0);
			p.setString(2, bookmark.getTitle());
			p.setString(3, bookmark.getDescription());
			p.setInt(4, bookmark.getId());
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
	 * @see net.jforum.dao.BookmarkDAO#remove(int)
	 */
	public void remove(int bookmarkId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("BookmarkModel.remove"));
			p.setInt(1, bookmarkId);
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
	 * @see net.jforum.dao.BookmarkDAO#selectByUser(int, int)
	 */
	public List<Bookmark> selectByUser(int userId, int relationType)
	{
		if (relationType == BookmarkType.FORUM) {
			return this.getForums(userId);
		}
		else if (relationType == BookmarkType.TOPIC) {
			return this.getTopics(userId);
		}
		else if (relationType == BookmarkType.USER) {
			return this.getUsers(userId);
		}
		else {
			throw new InvalidBookmarkTypeException("The type " + relationType + " is not a valid bookmark type");
		}
	}

	/**
	 * @see net.jforum.dao.BookmarkDAO#selectByUser(int)
	 */
	public List<Bookmark> selectByUser(int userId)
	{
		List<Bookmark> l = new ArrayList<Bookmark>();

		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("BookmarkModel.selectAllFromUser"));
			p.setInt(1, userId);

			rs = p.executeQuery();
			while (rs.next()) {
				l.add(this.getBookmark(rs));
			}

			return l;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.BookmarkDAO#selectById(int)
	 */
	public Bookmark selectById(int bookmarkId)
	{
		Bookmark bookmark = null;

		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("BookmarkModel.selectById"));
			p.setInt(1, bookmarkId);

			rs = p.executeQuery();
			if (rs.next()) {
				bookmark = this.getBookmark(rs);
			}

			return bookmark;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.BookmarkDAO#selectForUpdate(int, int, int)
	 */
	public Bookmark selectForUpdate(int relationId, int relationType, int userId)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("BookmarkModel.selectForUpdate"));
			p.setInt(1, relationId);
			p.setInt(2, relationType);
			p.setInt(3, userId);

			Bookmark bookmark = null;
			rs = p.executeQuery();
			if (rs.next()) {
				bookmark = this.getBookmark(rs);
			}

			return bookmark;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	protected List<Bookmark> getUsers(int userId)
	{
		List<Bookmark> l = new ArrayList<Bookmark>();
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("BookmarkModel.selectUserBookmarks"));
			p.setInt(1, userId);

			rs = p.executeQuery();
			while (rs.next()) {
				Bookmark bookmark = this.getBookmark(rs);

				if (bookmark.getTitle() == null || "".equals(bookmark.getTitle())) {
					bookmark.setTitle(rs.getString("username"));
				}

				l.add(bookmark);
			}

			return l;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	protected List<Bookmark> getTopics(int userId)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			List<Bookmark> l = new ArrayList<Bookmark>();
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("BookmarkModel.selectTopicBookmarks"));
			p.setInt(1, userId);

			rs = p.executeQuery();
			while (rs.next()) {
				Bookmark bookmark = this.getBookmark(rs);

				if (bookmark.getTitle() == null || "".equals(bookmark.getTitle())) {
					bookmark.setTitle(rs.getString("topic_title"));
				}

				l.add(bookmark);
			}

			return l;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	protected List<Bookmark> getForums(int userId)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			List<Bookmark> l = new ArrayList<Bookmark>();
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("BookmarkModel.selectForumBookmarks"));
			p.setInt(1, userId);

			rs = p.executeQuery();
			while (rs.next()) {
				Bookmark bookmark = this.getBookmark(rs);

				if (bookmark.getTitle() == null || "".equals(bookmark.getTitle())) {
					bookmark.setTitle(rs.getString("forum_name"));
				}

				if (bookmark.getDescription() == null || "".equals(bookmark.getDescription())) {
					bookmark.setDescription(rs.getString("forum_desc"));
				}

				l.add(bookmark);
			}

			return l;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	protected Bookmark getBookmark(ResultSet rs) throws SQLException
	{
		Bookmark bookmark = new Bookmark();
		bookmark.setId(rs.getInt("bookmark_id"));
		bookmark.setDescription(rs.getString("description"));
		bookmark.setPublicVisible(rs.getInt("public_visible") == 1);
		bookmark.setRelationId(rs.getInt("relation_id"));
		bookmark.setTitle(rs.getString("title"));
		bookmark.setDescription(rs.getString("description"));
		bookmark.setUserId(rs.getInt("user_id"));
		bookmark.setRelationType(rs.getInt("relation_type"));

		return bookmark;
	}
}

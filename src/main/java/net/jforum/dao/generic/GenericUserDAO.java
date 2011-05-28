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
 * Created on Apr 5, 2003 / 11:43:46 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.UserDAO;
import net.jforum.entities.Group;
import net.jforum.entities.KarmaStatus;
import net.jforum.entities.User;
import net.jforum.exceptions.DatabaseException;
import net.jforum.exceptions.ForumException;
import net.jforum.sso.LoginAuthenticator;
import net.jforum.util.DbUtils;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id$
 */
public class GenericUserDAO extends AutoKeys implements UserDAO
{
	private static LoginAuthenticator loginAuthenticator;

	public GenericUserDAO()
	{
		GenericUserDAO.setLoginAuthenticator();
		
		if (loginAuthenticator == null) {
			throw new ForumException("There is no login.authenticator configured. Check your login.authenticator configuration key.");
		}
		
		loginAuthenticator.setUserModel(this);
	}
	
	private static void setLoginAuthenticator() 
	{
		loginAuthenticator = (LoginAuthenticator)SystemGlobals.getObjectValue(
				ConfigKeys.LOGIN_AUTHENTICATOR_INSTANCE);			
	}
	
	/**
	 * @see net.jforum.dao.UserDAO#pendingActivations()
	 */
	public List<User> pendingActivations() 
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		
		List<User> l = new ArrayList<User>();
		
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
				SystemGlobals.getSql("UserModel.pendingActivations"));

			rs = p.executeQuery();

			while (rs.next()) {
				User user = new User();
				
				user.setId(rs.getInt("user_id"));
				user.setUsername(rs.getString("username"));
				user.setRegistrationDate(new Date(rs.getTimestamp("user_regdate").getTime()));
				
				l.add(user);
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
	 * @see net.jforum.dao.UserDAO#selectById(int)
	 */
	public User selectById(int userId)
	{
		String q = SystemGlobals.getSql("UserModel.selectById");
		PreparedStatement p = null;
		ResultSet rs = null;
		
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(q);
			p.setInt(1, userId);

			rs = p.executeQuery();
			User user = new User();

			if (rs.next()) {
				this.fillUserFromResultSet(user, rs);
				user.setPrivateMessagesCount(rs.getInt("private_messages"));

				rs.close();
				p.close();

				// User groups
				p = JForumExecutionContext.getConnection().prepareStatement(
						SystemGlobals.getSql("UserModel.selectGroups"));
				p.setInt(1, userId);

				rs = p.executeQuery();
				while (rs.next()) {
					Group g = new Group();
					g.setName(rs.getString("group_name"));
					g.setId(rs.getInt("group_id"));

					user.getGroupsList().add(g);
				}
			}

			return user;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	public User selectByName(String username)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectByName"));
			p.setString(1, username);

			rs = p.executeQuery();
			User user = null;

			if (rs.next()) {
				user = new User();
				this.fillUserFromResultSet(user, rs);
			}

			return user;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	protected void fillUserFromResultSet(User user, ResultSet rs) throws SQLException
	{
		user.setAim(rs.getString("user_aim"));
		user.setAvatar(rs.getString("user_avatar"));
		user.setGender(rs.getString("gender"));
		user.setRankId(rs.getInt("rank_id"));
		user.setThemeId(rs.getInt("themes_id"));
		user.setPrivateMessagesEnabled(rs.getInt("user_allow_pm") == 1);
		user.setNotifyOnMessagesEnabled(rs.getInt("user_notify") == 1);
		user.setViewOnlineEnabled(rs.getInt("user_viewonline") == 1);
		user.setPassword(rs.getString("user_password"));
		user.setViewEmailEnabled(rs.getInt("user_viewemail") == 1);
		user.setViewOnlineEnabled(rs.getInt("user_allow_viewonline") == 1);
		user.setAvatarEnabled(rs.getInt("user_allowavatar") == 1);
		user.setBbCodeEnabled(rs.getInt("user_allowbbcode") == 1);
		user.setHtmlEnabled(rs.getInt("user_allowhtml") == 1);
		user.setSmiliesEnabled(rs.getInt("user_allowsmilies") == 1);
		user.setEmail(rs.getString("user_email"));
		user.setFrom(rs.getString("user_from"));
		user.setIcq(rs.getString("user_icq"));
		user.setId(rs.getInt("user_id"));
		user.setInterests(rs.getString("user_interests"));
		user.setBiography(rs.getString("user_biography"));
		user.setLastVisit(rs.getTimestamp("user_lastvisit"));
		user.setOccupation(rs.getString("user_occ"));
		user.setTotalPosts(rs.getInt("user_posts"));
		user.setRegistrationDate(new Date(rs.getTimestamp("user_regdate").getTime()));
		user.setSignature(rs.getString("user_sig"));
		user.setWebSite(rs.getString("user_website"));
		user.setYim(rs.getString("user_yim"));
		user.setUsername(rs.getString("username"));
		user.setAttachSignatureEnabled(rs.getInt("user_attachsig") == 1);
		user.setMsnm(rs.getString("user_msnm"));
		user.setLang(rs.getString("user_lang"));
		user.setActive(rs.getInt("user_active"));
		user.setKarma(new KarmaStatus(user.getId(), rs.getDouble("user_karma")));
		user.setNotifyPrivateMessagesEnabled(rs.getInt("user_notify_pm") == 1);
		user.setDeleted(rs.getInt("deleted"));
		user.setNotifyAlways(rs.getInt("user_notify_always") == 1);
		user.setNotifyText(rs.getInt("user_notify_text") == 1);

		String actkey = rs.getString("user_actkey");
		user.setActivationKey(actkey == null || "".equals(actkey) ? null : actkey);
	}

	/**
	 * @see net.jforum.dao.UserDAO#delete(int)
	 */
	public void delete(int userId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("UserModel.deletedStatus"));
			p.setInt(1, 1);
			p.setInt(2, userId);

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
	 * @see net.jforum.dao.UserDAO#update(net.jforum.entities.User)
	 */
	public void update(User user)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.update"));

			p.setString(1, user.getAim());
			p.setString(2, user.getAvatar());
			p.setString(3, user.getGender());
			p.setInt(4, user.getThemeId());
			p.setInt(5, user.isPrivateMessagesEnabled() ? 1 : 0);
			p.setInt(6, user.isAvatarEnabled() ? 1 : 0);
			p.setInt(7, user.isBbCodeEnabled() ? 1 : 0);
			p.setInt(8, user.isHtmlEnabled() ? 1 : 0);
			p.setInt(9, user.isSmiliesEnabled() ? 1 : 0);
			p.setString(10, user.getEmail());
			p.setString(11, user.getFrom());
			p.setString(12, user.getIcq());
			p.setString(13, user.getInterests());
			p.setString(14, user.getOccupation());
			p.setString(15, user.getSignature());
			p.setString(16, user.getWebSite());
			p.setString(17, user.getYim());
			p.setString(18, user.getMsnm());
			p.setString(19, user.getPassword());
			p.setInt(20, user.isViewEmailEnabled() ? 1 : 0);
			p.setInt(21, user.isViewOnlineEnabled() ? 1 : 0);
			p.setInt(22, user.isNotifyOnMessagesEnabled() ? 1 : 0);
			p.setInt(23, user.isAttachSignatureEnabled() ? 1 : 0);
			p.setString(24, user.getUsername());
			p.setString(25, user.getLang());
			p.setInt(26, user.isNotifyPrivateMessagesEnabled() ? 1 : 0);
			p.setString(27, user.getBiography());

			if (user.getLastVisit() == null) {
				user.setLastVisit(new Date());
			}

			p.setTimestamp(28, new Timestamp(user.getLastVisit().getTime()));
			p.setInt(29, user.notifyAlways() ? 1 : 0);
			p.setInt(30, user.notifyText() ? 1 : 0);
			p.setInt(31, user.getRankId());
			p.setInt(32, user.getId());

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
	 * @see net.jforum.dao.UserDAO#addNew(net.jforum.entities.User)
	 */
	public int addNew(User user)
	{
		PreparedStatement p = null;
		try {
			p = this.getStatementForAutoKeys("UserModel.addNew");

			this.initNewUser(user, p);

			this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("UserModel.lastGeneratedUserId"));
			int id = this.executeAutoKeysQuery(p);

			this.addToGroup(id, new int[] { SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP) });

			user.setId(id);
			return id;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(p);
		}
	}

	protected void initNewUser(User user, PreparedStatement p) throws SQLException
	{
		p.setString(1, user.getUsername());
		p.setString(2, user.getPassword());
		p.setString(3, user.getEmail());
		p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		p.setString(5, user.getActivationKey());
	}

	/**
	 * @see net.jforum.dao.UserDAO#addNewWithId(net.jforum.entities.User)
	 */
	public void addNewWithId(User user)
	{
		PreparedStatement p = null;
		try {
			p = this.getStatementForAutoKeys("UserModel.addNewWithId");

			this.initNewUser(user, p);
			p.setInt(6, user.getId());

			p.executeUpdate();

			this.addToGroup(user.getId(), new int[] { SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP) });
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#decrementPosts(int)
	 */
	public void decrementPosts(int userId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.decrementPosts"));
			p.setInt(1, userId);

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
	 * @see net.jforum.dao.UserDAO#incrementPosts(int)
	 */
	public void incrementPosts(int userId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.incrementPosts"));
			p.setInt(1, userId);

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
	 * @see net.jforum.dao.UserDAO#setRanking(int, int)
	 */
	public void setRanking(int userId, int rankingId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.rankingId"));
			p.setInt(1, rankingId);
			p.setInt(2, userId);

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
	 * @see net.jforum.dao.UserDAO#setActive(int, boolean)
	 */
	public void setActive(int userId, boolean active)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.activeStatus"));
			p.setInt(1, active ? 1 : 0);
			p.setInt(2, userId);

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
	 * @see net.jforum.dao.UserDAO#undelete(int)
	 */
	public void undelete(int userId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection()
					.prepareStatement(SystemGlobals.getSql("UserModel.deletedStatus"));
			p.setInt(1, 0);
			p.setInt(2, userId);

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
	 * @see net.jforum.dao.UserDAO#selectAll()
	 */
	public List<User> selectAll()
	{
		return selectAll(0, 0);
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAll(int, int)
	 */
	public List<User> selectAll(int startFrom, int count)
	{
		PreparedStatement p = null;
		ResultSet rs = null;

		try {
			if (count > 0) {
				p = JForumExecutionContext.getConnection().prepareStatement(
						SystemGlobals.getSql("UserModel.selectAllByLimit"));
				p.setInt(1, startFrom);
				p.setInt(2, count);
			}
			else {
				p = JForumExecutionContext.getConnection()
						.prepareStatement(SystemGlobals.getSql("UserModel.selectAll"));
			}

			rs = p.executeQuery();

			return this.processSelectAll(rs);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAllWithKarma()
	 */
	public List<User> selectAllWithKarma()
	{
		return this.selectAllWithKarma(0, 0);
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAllWithKarma(int, int)
	 */
	public List<User> selectAllWithKarma(int startFrom, int count)
	{
		return this.loadKarma(this.selectAll(startFrom, count));
	}

	protected List<User> processSelectAll(ResultSet rs) throws SQLException
	{
		List<User> list = new ArrayList<User>();

		while (rs.next()) {
			User user = new User();

			user.setEmail(rs.getString("user_email"));
			user.setId(rs.getInt("user_id"));
			user.setTotalPosts(rs.getInt("user_posts"));
			user.setRegistrationDate(new Date(rs.getTimestamp("user_regdate").getTime()));
			user.setUsername(rs.getString("username"));
			user.setDeleted(rs.getInt("deleted"));
			KarmaStatus karma = new KarmaStatus();
			karma.setKarmaPoints(rs.getInt("user_karma"));
			user.setKarma(karma);
			user.setFrom(rs.getString("user_from"));
			user.setWebSite(rs.getString("user_website"));
			user.setViewEmailEnabled(rs.getInt("user_viewemail") == 1);

			list.add(user);
		}

		return list;
	}

	/**
	 * @see net.jforum.dao.UserDAO#selectAllByGroup(int, int, int)
	 */
	public List<User> selectAllByGroup(int groupId, int start, int count)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.selectAllByGroup"));
			p.setInt(1, groupId);
			p.setInt(2, start);
			p.setInt(3, count);

			rs = p.executeQuery();

			return this.processSelectAll(rs);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#getLastUserInfo()
	 */
	public User getLastUserInfo()
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			User user = new User();

			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.lastUserRegistered"));
			rs = p.executeQuery();
			rs.next();

			user.setUsername(rs.getString("username"));
			user.setId(rs.getInt("user_id"));

			return user;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#getTotalUsers()
	 */
	public int getTotalUsers()
	{
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.totalUsers"));
			return this.getTotalUsersCommon(preparedStatement);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(preparedStatement);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#getTotalUsersByGroup(int)
	 */
	public int getTotalUsersByGroup(int groupId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.totalUsersByGroup"));
			p.setInt(1, groupId);

			return this.getTotalUsersCommon(p);
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(p);
		}
	}

	protected int getTotalUsersCommon(PreparedStatement p) throws SQLException
	{
		ResultSet rs = p.executeQuery();
		int total = 0;
		if (rs.next()) {
		    total = rs.getInt(1);
		}
		rs.close();
		p.close();

		return total;
	}

	/**
	 * @see net.jforum.dao.UserDAO#isDeleted(int user_id)
	 */
	public boolean isDeleted(int userId)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.isDeleted"));
			p.setInt(1, userId);

			int deleted = 0;

			rs = p.executeQuery();
			if (rs.next()) {
				deleted = rs.getInt("deleted");
			}

			return deleted == 1;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#isUsernameRegistered(java.lang.String)
	 */
	public boolean isUsernameRegistered(String username)
	{
		boolean status = false;

		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.isUsernameRegistered"));
			p.setString(1, username);

			rs = p.executeQuery();
			if (rs.next() && rs.getInt("registered") > 0) {
				status = true;
			}

			return status;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#validateLogin(java.lang.String, java.lang.String)
	 */
	public User validateLogin(String username, String password)
	{
		return loginAuthenticator.validateLogin(username, password, null);
	}

	/**
	 * @see net.jforum.dao.UserDAO#addToGroup(int, int[])
	 */
	public void addToGroup(int userId, int[] groupId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.addToGroup"));
			p.setInt(1, userId);

			for (int i = 0; i < groupId.length; i++) {
				p.setInt(2, groupId[i]);
				p.executeUpdate();
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(p);
		}

	}

	/**
	 * @see net.jforum.dao.UserDAO#removeFromGroup(int, int[])
	 */
	public void removeFromGroup(int userId, int[] groupId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.removeFromGroup"));
			p.setInt(1, userId);

			for (int i = 0; i < groupId.length; i++) {
				p.setInt(2, groupId[i]);
				p.executeUpdate();
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#saveNewPassword(java.lang.String, java.lang.String)
	 */
	public void saveNewPassword(String password, String email)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.saveNewPassword"));
			p.setString(1, password);
			p.setString(2, email);
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
	 * @see net.jforum.dao.UserDAO#validateLostPasswordHash(java.lang.String, java.lang.String)
	 */
	public boolean validateLostPasswordHash(String email, String hash)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.validateLostPasswordHash"));
			p.setString(1, hash);
			p.setString(2, email);

			boolean status = false;

			rs = p.executeQuery();
			if (rs.next() && rs.getInt("valid") > 0) {
				status = true;

				this.writeLostPasswordHash(email, "");
			}

			return status;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#writeLostPasswordHash(java.lang.String, java.lang.String)
	 */
	public void writeLostPasswordHash(String email, String hash)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.writeLostPasswordHash"));
			p.setString(1, hash);
			p.setString(2, email);
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
	 * @see net.jforum.dao.UserDAO#getUsernameByEmail(java.lang.String)
	 */
	public String getUsernameByEmail(String email)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.getUsernameByEmail"));
			p.setString(1, email);

			String username = "";

			rs = p.executeQuery();
			if (rs.next()) {
				username = rs.getString("username");
			}

			return username;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#findByName(java.lang.String, boolean)
	 */
	public List<User> findByName(String input, boolean exactMatch)
	{
		List<User> namesList = new ArrayList<User>();

		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.findByName"));
			p.setString(1, exactMatch ? input : "%" + input + "%");

			rs = p.executeQuery();
			while (rs.next()) {
				User user = new User();

				user.setId(rs.getInt("user_id"));
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("user_email"));
				user.setDeleted(rs.getInt("deleted"));

				namesList.add(user);
			}
			return namesList;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#validateActivationKeyHash(int, java.lang.String)
	 */
	public boolean validateActivationKeyHash(int userId, String hash)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.validateActivationKeyHash"));
			p.setString(1, hash);
			p.setInt(2, userId);

			boolean status = false;

			rs = p.executeQuery();
			if (rs.next() && rs.getInt("valid") == 1) {
				status = true;
			}

			return status;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * @see net.jforum.dao.UserDAO#writeUserActive(int)
	 */
	public void writeUserActive(int userId)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.writeUserActive"));
			p.setInt(1, userId);
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
	 * @see net.jforum.dao.UserDAO#updateUsername(int, String)
	 */
	public void updateUsername(int userId, String username)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.updateUsername"));
			p.setString(1, username);
			p.setInt(2, userId);
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
	 * @see net.jforum.dao.UserDAO#hasUsernameChanged(int, java.lang.String)
	 */
	public boolean hasUsernameChanged(int userId, String usernameToCheck)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.getUsername"));
			p.setString(1, usernameToCheck);
			p.setInt(2, userId);

			String dbUsername = null;

			rs = p.executeQuery();
			if (rs.next()) {
				dbUsername = rs.getString("username");
			}

			boolean status = false;

			if (!usernameToCheck.equals(dbUsername)) {
				status = true;
			}

			return status;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}

	/**
	 * Load KarmaStatus from a list of users.
	 * 
	 * @param users
	 *            List
	 * @return List
	 * @throws SQLException
	 */
	protected List<User> loadKarma(List<User> users)
	{
		List<User> result = new ArrayList<User>(users.size());

		for (Iterator<User> iter = users.iterator(); iter.hasNext();) {
			User user = iter.next();

			// Load Karma
			DataAccessDriver.getInstance().newKarmaDAO().getUserTotalKarma(user);
			result.add(user);
		}

		return result;
	}

	/**
	 * @see net.jforum.dao.UserDAO#saveUserAuthHash(int, java.lang.String)
	 */
	public void saveUserAuthHash(int userId, String hash)
	{
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.saveUserAuthHash"));
			p.setString(1, hash);
			p.setInt(2, userId);
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
	 * @see net.jforum.dao.UserDAO#getUserAuthHash(int)
	 */
	public String getUserAuthHash(int userId)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.getUserAuthHash"));
			p.setInt(1, userId);

			rs = p.executeQuery();

			String hash = null;
			if (rs.next()) {
				hash = rs.getString(1);
			}

			return hash;
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
	}
	
	/**
	 * @see net.jforum.dao.UserDAO#findByEmail(java.lang.String)
	 */
	public User findByEmail(String email)
	{
		PreparedStatement p = null;
		ResultSet rs = null;
		
		User user = null;
		
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("UserModel.findByEmail"));
			p.setString(1, email);
			rs = p.executeQuery();
			
			if (rs.next()) {
				user = new User();
				fillUserFromResultSet(user, rs);
			}
		}
		catch (SQLException e) {
			throw new DatabaseException(e);
		}
		finally {
			DbUtils.close(rs, p);
		}
		
		return user;
	}
}

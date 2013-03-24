/*
 * Copyright (c) JForum Team
 * All rights reserved.

 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:

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
 * Created on 25/02/2004 - 19:16:25
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.jforum.dao.DataAccessDriver;
import net.jforum.entities.User;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 * @version $Id$
 */
public class SearchArgs 
{
	private String keywords;
	private int userID;
	private String orderDir = "DESC";
	private String orderBy;
	private int forumId;
	private int initialRecord;
	private Date fromDate;
	private Date toDate;
	private MatchType matchType;
	private String searchIn;
	private String searchDate;
	private boolean groupByForum;

	// member search
	private int memberId;
	private String memberName;
	private boolean topicsIstarted;
	private String memberMatchType;

	public static enum MatchType {
		ALL_KEYWORDS,
		ANY_KEYWORDS,
		EXACT_PHRASE,
		RAW_KEYWORDS
	}

	/**
	 * set the matching type - default to all words if unknown string is passed
	 * @param matchType
	 */
	public void setMatchType(String matchType)
	{
		if ("any".equals(matchType)) {
			this.matchType = MatchType.ANY_KEYWORDS;
		} else if ("raw".equals(matchType)) {
			this.matchType = MatchType.RAW_KEYWORDS;
		} else if ("phrase".equals(matchType)) {
			this.matchType = MatchType.EXACT_PHRASE;
		} else {
			/* not set or null like for new messagesSearch */
			this.matchType = MatchType.ALL_KEYWORDS;
		}
	}

	public boolean isMatchAll() {
		return matchType.equals(MatchType.ALL_KEYWORDS);
	}

	public boolean isMatchAny() {
		return matchType.equals(MatchType.ANY_KEYWORDS);
	}

	public boolean isMatchExact() {
		return matchType.equals(MatchType.EXACT_PHRASE);
	}

	public boolean isMatchRaw() {
		return matchType.equals(MatchType.RAW_KEYWORDS);
	}

	public void setDateRange(Date fromDate, Date toDate)
	{
		this.fromDate = fromDate;
		this.toDate = toDate;
	}

	public Date getFromDate()
	{
		return this.fromDate;
	}

	public Date getToDate()
	{
		return this.toDate;
	}

	public String getSearchDate() {
		return formatNullOrTrim(searchDate);
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}

	public int fetchCount()
	{
		return SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
	}

	public void startFetchingAtRecord(int initialRecord)
	{
		this.initialRecord = initialRecord;
	}

	public int startFrom()
	{
		return this.initialRecord;
	}

	public void setKeywords(String keywords)
	{
		this.keywords = keywords;
	}

	public void setMemberName (String memberName)
	{
		this.memberName = memberName;
	}

	// -----------------------------------------------------------------

	/**
	 * Return member ids to query.<br />
	 * 1) If user searching by keyword and not member, return empty array.
	 * <br />
	 * 2) If user searching by user id, return array with single element <br />
	 * 3) If user searching by display name, return array with all non-negative
	 * elements. (Lucene doesn't allow searches for negative numbers without
	 * custom code. Since negative numbers are only test ids (and
	 * anonymous/admin), the search is not present for negative ids.
	 */
	public int[] getMemberIds() {
		int[] memberIds;
		if (memberId > 0) {
			memberIds = new int[] { memberId };
		} else if (memberName != null && memberName.trim().length() > 0) {
			memberIds = getMemberIdsForName();
		} else {
			memberIds = new int[0];
		}
		return memberIds;
	}

	private int[] getMemberIdsForName() {
		int[] memberIds;
		List<User> users = DataAccessDriver.getInstance().newUserDAO().findByName(getMemberName(), false);
		users = removeNegativeIds(users);
		int length = users.size();
		memberIds = new int[length];
		for (int i = 0; i < length; i++) {
			memberIds[i] = users.get(i).getId();
		}
		return memberIds;
	}

	/*
	 * Lucene doesn't handle negative numbers. Since the only negative ids are
	 * test ids (or anonymous or admin), real users won't be searching for them.
	 */
	private List<User> removeNegativeIds(List<User> list) {
		List<User> result = new ArrayList<User>(list.size());
		for (User user : list) {
			if (user.getId() >= 0) {
				result.add(user);
			}
		}
		return result;
	}

	public void setMemberId(String memberId) {
		int id = -1;
		if (memberId != null && memberId.trim().length() > 0) {
			try {
				id = Integer.parseInt(memberId.trim());
			} catch (NumberFormatException nfex) {
				// id is already -1, no need to do anything about this
			}
		}

		this.memberId = id;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public void setTopicsIstarted(boolean topicsIstarted) {
		this.topicsIstarted = topicsIstarted;
	}

	public boolean isTopicsIstarted() {
		return topicsIstarted;
	}

	public void setForumId(int forumId)
	{
		this.forumId = forumId;
	}

	public void setUserID(int userID)
	{
		this.userID = userID;
	}

	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

	public void setOrderDir(String orderDir)
	{
		if (orderDir != null && (orderDir.equals("ASC") || orderDir.equals("DESC")))
			this.orderDir = orderDir;
	}

	public boolean isGroupByForum() {
		return groupByForum;
	}

	public void setGroupByForum(boolean groupByForum) {
		this.groupByForum = groupByForum;
	}

	public String[] getKeywords()
	{
		if (this.keywords == null || this.keywords.trim().length() == 0) {
			return new String[] {};
		}

		return this.keywords.trim().split(" ");
	}

	public String rawKeywords()
	{
		if (this.keywords == null) {
			return "";
		}

		return this.keywords.trim();
	}

	public int getUserID() {
		return userID;
	}

	public String getMemberName()
	{
		return this.memberName;
	}

	public int getForumId()
	{
		return this.forumId;
	}

	public boolean isOrderDirectionDescending() {
		return "DESC".equals(orderDir);
	}

	public String getOrderBy()
	{
		return this.orderBy;
	}

	// -----------------------------------------------------------------

	public String getMemberMatchType() {
		return formatNullOrTrim(memberMatchType);
	}

	public void setMemberMatchType(String memberMatchType) {
		this.memberMatchType = memberMatchType;
	}

	public boolean shouldLimitSearchToTopicStarted() {
		return "memberStarted".equals(memberMatchType);
	}

	// -----------------------------------------------------------------

	public String getSearchIn() {
		return formatNullOrTrim(searchIn);
	}

	public void setSearchIn(String searchIn) {
		this.searchIn = searchIn;
	}

	public boolean shouldLimitSearchToSubject() {
		return "SUBJECT".equals(searchIn);
	}

	// -----------------------------------------------------------------

	private String formatNullOrTrim(String value) {
		if (value == null) {
			return "";
		}
		return value.trim();
	}
}

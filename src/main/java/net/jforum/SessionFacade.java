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
 * Created on 12/03/2004 - 18:47:26
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import net.jforum.cache.CacheEngine;
import net.jforum.cache.Cacheable;
import net.jforum.dao.DataAccessDriver;
import net.jforum.entities.UserSession;
import net.jforum.repository.SecurityRepository;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class SessionFacade implements Cacheable
{
	private static final Logger LOGGER = Logger.getLogger(SessionFacade.class);

	// map session IDs to UserSession objects
	// creating new String object because we're synchronizing on it
	private static final String FQN = new String("sessions");
	// map session IDs to UserSession objects of whose online status is publicly visible.
	private static final String FQN_LOGGED = FQN + "/logged";
	// map different types of user count types (string names) to int user counts 
	private static final String FQN_COUNT = FQN + "/count";
    // map user IDs (int) to a set of session IDs (Set<String>)
	private static final String FQN_USER_ID = FQN + "/userId";
	private static final String ANONYMOUS_COUNT = "anonymousCount";
	private static final String LOGGED_COUNT = "loggedCount";
	
	private static CacheEngine cache;
	
	/**
	 * @see net.jforum.cache.Cacheable#setCacheEngine(net.jforum.cache.CacheEngine)
	 */
	@Override public void setCacheEngine(final CacheEngine engine)
	{
		SessionFacade.setEngine(engine);
	}
	
	private static void setEngine(final CacheEngine engine) 
	{
		cache = engine;
	}

	//TODO hack so csrf can access cache
	public static UserSession getUserSesssion (String sessionId) {
	    return (UserSession) cache.get(FQN, sessionId);
	}

	/**
	 * Add a new <code>UserSession</code> entry to the session.
	 * This method will make a call to <code>JForum.getRequest.getSession().getId()</code>
	 * to retrieve the session's id
	 * 
	 * @param userSession The user session object to add
	 * @see #add(UserSession, String)
	 */
	public static void add(final UserSession userSession)
	{
		add(userSession, JForumExecutionContext.getRequest().getSessionContext().getId());
	}

	/**
	 * Registers a new {@link UserSession}.
	 * <p>
	 * If a call to {@link UserSession#getUserId()} return a value different 
	 * of <code>SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)</code>, then 
	 * the user will be registered as "logged". Otherwise it will enter as anonymous.
	 * </p>
	 * 
	 * <p>
	 * Please note that, in order to keep the number of guest and logged users correct, 
	 * it's caller's responsibility to {@link #remove(String)} the record before adding it
	 * again if the current session is currently represented as "guest". 
	 * </p>
	 *  
	 * @param userSession the UserSession to add
	 * @param sessionId the user's session id
	 */

	public static void add(UserSession us, String sessionId) {
		if (us.getSessionId() == null || us.getSessionId().equals("")) {
			us.setSessionId(sessionId);
		}
		
		final String usSessId = us.getSessionId();

		synchronized (FQN) {
			cache.add(FQN, usSessId, us);

			if (!JForumExecutionContext.getForumContext().isBot()) {
				if (us.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
					changeUserCount(LOGGED_COUNT, true);

					cache.add(FQN_LOGGED, usSessId, us);

					final String userIdStr = String.valueOf(us.getUserId()); 

					Set<String> sessIds = (Set<String>)cache.get(FQN_USER_ID, userIdStr);
					if (sessIds == null) {
					    sessIds = new HashSet<>();
					}

					if (!sessIds.contains(usSessId)) {
					    sessIds.add(usSessId);
					}

					cache.add(FQN_USER_ID, userIdStr, sessIds);
				} else {
					// TODO: check the anonymous IP constraint
					changeUserCount(ANONYMOUS_COUNT, true);
				}
			}
		}
	}

	private static void changeUserCount(final String cacheEntryName, final boolean increment)
	{
		Integer count = (Integer)cache.get(FQN_COUNT, cacheEntryName);
		
		if (count == null) {
			count = Integer.valueOf(0);
		}
		
		if (increment) {
			count = Integer.valueOf(count.intValue() + 1);
		}
		else if (count.intValue() > 0) {
			count = Integer.valueOf(count.intValue() - 1);
		}
		
		cache.add(FQN_COUNT, cacheEntryName, count);
	}
	
	/**
	 * Add a new entry to the user's session
	 * 
	 * @param name The attribute name
	 * @param value The attribute value
	 */
	public static void setAttribute(final String name, final Object value)
	{
		JForumExecutionContext.getRequest().getSessionContext().setAttribute(name, value);
	}
	
	/**
	 * Removes an attribute from the session
	 * 
	 * @param name The key associated to the the attribute to remove
	 */
	public static void removeAttribute(final String name)
	{
		JForumExecutionContext.getRequest().getSessionContext().removeAttribute(name);
	}
	
	/**
	 * Gets an attribute value given its name
	 * 
	 * @param name The attribute name to retrieve the value
	 * @return The value as an Object, or null if no entry was found
	 */
	public static Object getAttribute(final String name)
	{
		return JForumExecutionContext.getRequest().getSessionContext().getAttribute(name);
	}

	/**
	 * Remove an entry from the session map
	 * 
	 * @param sessionId The session id to remove
	 */

	public static void remove(String sessionId) {
		if (cache == null) {
			LOGGER.warn("Got a null cache instance. #" + sessionId);
			return;
		}

		synchronized (FQN) {
			UserSession us = getUserSession(sessionId);

			if (us != null) {
				cache.remove(FQN_LOGGED, sessionId);
				removeSpecificUserSession(us.getUserId(), sessionId);

				if (us.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
					changeUserCount(LOGGED_COUNT, false);
				}
				else {
					changeUserCount(ANONYMOUS_COUNT, false);
				}
			}

			cache.remove(FQN, sessionId);
		}
	}
	
	public static void removeUserSessions(int userId) {
	    final String userIdStr = String.valueOf(userId);
	    Set<String> sessIds = (Set<String>)cache.get(FQN_USER_ID, userIdStr);
        if(sessIds != null) {
            for(final String sessId: sessIds) {
                remove(sessId);
            }
            cache.remove(FQN_USER_ID, userIdStr);
        }
	}

	private static void removeSpecificUserSession(int userId, String sessionId) {
	    final String userIdStr = String.valueOf(userId);
	    Set<String> sessIds = (Set<String>) cache.get(FQN_USER_ID, userIdStr);
	    if (sessIds != null) {
	        // remove if we have a session with the given session ID
	        if (sessIds.removeIf(s -> s.equals(sessionId))) {
	            // if removing caused the set to be empty (i.e.- we had only one session per user),
	            // remove the whole cache entry for this user. Otherwise, add the new session to the cache
	            if (sessIds.isEmpty()) {
	                cache.remove(FQN_USER_ID, userIdStr);
	            } else {
	                cache.add(FQN_USER_ID, userIdStr, sessIds);
	            }
	        }
	    }
	}

	/**
	 * Get all registered sessions
	 * 
	 * @return <code>ArrayList</code> with the sessions. Each entry
	 * is a <code>UserSession</code> object.
	 */
	public static List<UserSession> getAllSessions()
	{
		synchronized (FQN) {
			Collection<Object> values = cache.getValues(FQN);
			ArrayList<UserSession> list = new ArrayList<>();
			for (Iterator<?> iter = values.iterator(); iter.hasNext(); ) {
				list.add((UserSession)iter.next());
			}
			return list;
		}
	}
	
	/**
	 * Gets the {@link UserSession} instance of all logged users
	 * @return A list with the user sessions
	 */
	public static List<UserSession> getLoggedSessions()
	{
		synchronized (FQN) {
			Collection<Object> values = cache.getValues(FQN_LOGGED);
			ArrayList<UserSession> list = new ArrayList<>();
			for (Iterator<?> iter = values.iterator(); iter.hasNext(); ) {
				list.add((UserSession)iter.next());
			}			
			return list;
		}
	}
	
	/**
	 * Get the number of logged users
	 * @return the number of logged users
	 */
	public static int registeredSize()
	{
		final Integer count = (Integer)cache.get(FQN_COUNT, LOGGED_COUNT);

		return (count == null ? 0 : count.intValue());
	}
	
	/**
	 * Get the number of anonymous users
	 * @return the number of anonymous users
	 */
	public static int anonymousSize()
	{
		final Integer count = (Integer)cache.get(FQN_COUNT, ANONYMOUS_COUNT);

		return (count == null ? 0 : count.intValue());
	}
	
	public static void clear()
	{
		synchronized (FQN) {
			cache.add(FQN, new ConcurrentHashMap<String, UserSession>());
			cache.add(FQN_COUNT, LOGGED_COUNT, Integer.valueOf(0));
			cache.add(FQN_COUNT, ANONYMOUS_COUNT, Integer.valueOf(0));
			cache.remove(FQN_LOGGED);
			cache.remove(FQN_USER_ID);
		}
	}
	
	/**
	 * Gets the user's <code>UserSession</code> object
	 * 
	 * @return The <code>UserSession</code> associated to the user's session
	 */
	public static UserSession getUserSession()
	{
		return getUserSession(JForumExecutionContext.getRequest().getSessionContext().getId());
	}
	
	/**
	 * Gets a {@link UserSession} by the session id.
	 * 
	 * @param sessionId the session's id
	 * @return an <b>immutable</b> UserSession, or <code>null</code> if no entry found
	 */
	public static UserSession getUserSession(final String sessionId)
	{
		UserSession userSession = null;
		if (cache == null) {
			LOGGER.warn("Got a null cache in getUserSession. #" + sessionId);			
		} else {
			userSession = (UserSession)cache.get(FQN, sessionId);
		}
		return userSession;
	}

	/**
	 * Gets the number of session elements.
	 * 
	 * @return The number of session elements currently online (without bots)
	 */
	public static int size()
	{
		return (anonymousSize() + registeredSize());
	}

	/**
	 * Verify if there is an user in the session with the user id passed as parameter.
	 * 
	 * @param userId The user id to check for existence in the session
	 * @return A set of session ids associated to this user, if the user is
	 *         already registered into at least one session, or an empty set if it is not.
	 */
	public static Set<String> findSessionIdsOfUser(int userId) {
		Set<String> val = (Set<String>) cache.get(FQN_USER_ID, Integer.toString(userId));
		return val != null ? val : Collections.emptySet();
	}

	/**
	 * Verify is the user is logged in.
	 * 
	 * @return <code>true</code> if the user is logged, or <code>false</code> if is an anonymous user.
	 */
	public static boolean isLogged()
	{
		return "1".equals(SessionFacade.getAttribute(ConfigKeys.LOGGED));
	}
	
	/**
	 * Marks the current user session as "logged" in 
	 */
	public static void makeLogged()
	{
		SessionFacade.setAttribute(ConfigKeys.LOGGED, "1");
	}
	
	/**
	 * Marks the current user session as "logged" out
	 *
	 */
	public static void makeUnlogged()
	{
		SessionFacade.removeAttribute(ConfigKeys.LOGGED);
		SessionFacade.removeAttribute(ConfigKeys.LAST_POST_TIME);
	}
	
	/**
	 * Returns a map containing information about read time of a set of topics.
	 * @return a map where the key is the topicId represented as an Integer, and the
	 * value is a Long representing the read time of such topic. 
	 */
	public static Map<Integer, Long> getTopicsReadTime()
	{
		Map<Integer, Long> tracking = (Map<Integer, Long>)getAttribute(ConfigKeys.TOPICS_READ_TIME);
		
		if (tracking == null) {
			tracking = new ConcurrentHashMap<>();
			setAttribute(ConfigKeys.TOPICS_READ_TIME, tracking);
		}
		
		return tracking;
	}
	
	/**
	 * Returns a map with "all topics read" flags for some forum 
	 * @return a map where the key is the forum id represented as an Integer, 
	 * and the value is a Long representing the read time to be used in the verifications.
	 */
	public static Map<Integer, Long> getTopicsReadTimeByForum()
	{
		return (Map<Integer, Long>)getAttribute(ConfigKeys.TOPICS_READ_TIME_BY_FORUM);
	}

	/**
	 * Persists user session information.
	 * 
	 * @param sessionId The session which we're going to persist the database. 
	 * @see #storeSessionData(String)
	 */
	public static void storeSessionData (String sessionId) {
		UserSession us = SessionFacade.getUserSession(sessionId);
		if (us != null) {
			try {
				if (us.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
					DataAccessDriver.getInstance().newUserSessionDAO().update(us, JForumExecutionContext.getConnection());
				}
				SecurityRepository.remove(us.getUserId());
			}
			catch (Exception e) {
				LOGGER.warn("Error storing user session data: " + e, e);
			}
		}
	}
	
	public static void storeSessionData (UserSession us) {
	    if (us != null) {
            try {
                if (us.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
                    DataAccessDriver.getInstance().newUserSessionDAO().update(us, JForumExecutionContext.getConnection());
                }
            }
            catch (Exception e) {
                LOGGER.warn("Error storing user session data: " + e, e);
            }
        }
	}

}

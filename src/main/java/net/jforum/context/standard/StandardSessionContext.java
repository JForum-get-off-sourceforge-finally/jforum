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
 * Created on 26/08/2006 21:55:33
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.context.standard;

import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import net.jforum.context.SessionContext;

/**
 * Session Context non-dependent of HTTP requests
 * @author Rafael Steil
 */
public class StandardSessionContext implements SessionContext
{
	private static final SecureRandom random = new SecureRandom();
	public static final String SESSION_ID = "__sessionId";
	private transient final ConcurrentHashMap<String, Object> data;
	
	public StandardSessionContext()
	{
		this.data = new ConcurrentHashMap<>();
		this.createSessionId();
	}

	private void createSessionId()
	{
		this.data.put(SESSION_ID, Integer.toString(random.nextInt(99999999)));
	}
	
	/**
	 * @see net.jforum.context.SessionContext#getAttribute(java.lang.String)
	 */
	@Override public Object getAttribute(final String name)
	{
		return this.data.get(name);
	}

	/**
	 * @see net.jforum.context.SessionContext#getAttributeNames()
	 */
	@Override public Enumeration<String> getAttributeNames()
	{
		return this.data.keys();
	}

	/**
	 * @see net.jforum.context.SessionContext#getId()
	 */
	@Override public String getId()
	{
		return (String)this.getAttribute(SESSION_ID);
	}

	/**
	 * @see net.jforum.context.SessionContext#invalidate()
	 */
	@Override public void invalidate()
	{
		this.data.clear();
		this.createSessionId();
	}

	/**
	 * @see net.jforum.context.SessionContext#removeAttribute(java.lang.String)
	 */
	@Override public void removeAttribute(final String name)
	{
		this.data.remove(name);
	}

	/**
	 * @see net.jforum.context.SessionContext#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override public void setAttribute(final String name, final Object value)
	{
		if (this.data.containsKey(name)) {
			this.data.remove(name);
		}
		
		this.data.put(name, value);
	}
}

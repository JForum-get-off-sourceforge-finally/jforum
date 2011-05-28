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
 * Created on 2009/8/3 8:42:57 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Andowson Chang
 * @version $Id$
 */
public class SortedProperties extends Properties
{
	private static final long serialVersionUID = -9142524044944196840L;
	private Set<String> names;

	public SortedProperties() {
		super();
		names = new TreeSet<String>();
	}
	
	public void list(final PrintStream out) {
		final Enumeration<String> enu = (Enumeration<String>) propertyNames();
		while (enu.hasMoreElements()) {
			final String name = enu.nextElement();
			out.println(name + "=" + getProperty(name));
		}
	}
	
	public void list(final PrintWriter out) {
		final Enumeration<String> enu = (Enumeration<String>) propertyNames();
		while (enu.hasMoreElements()) {
			final String name = enu.nextElement();
			out.println(name + "=" + getProperty(name));
		}
	}
	
	public Enumeration<String> propertyNames() {
		return Collections.enumeration(names);
	}
	
	 public Object put(final String key, final String value) {
		 if (names.contains(key)) {
			 names.remove(key);
		 }
		 names.add(key);
		 
		 return super.put(key, value);
	 }
	 
	 public Object remove(final Object key) {
		 names.remove(key);
		 
		 return super.remove(key);
	 }
	 
	 public boolean equals(final Object obj) {
		 return (obj instanceof SortedProperties) && super.equals(obj);
	 }
	 
	 public int hashCode() {
		 return super.hashCode();
	 }
}
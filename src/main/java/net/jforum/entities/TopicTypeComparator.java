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
 * Created on 29/04/2006 10:41:02
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Rafael Steil
 */
public class TopicTypeComparator implements Comparator<Topic>, Serializable
{
	private static final long serialVersionUID = 4774281835148485281L;

	private boolean ignoreStickyAnnounce = false;

	public TopicTypeComparator() {
		this(false);
	}

	// sticky and announce types are ignored for recent topics listings
	public TopicTypeComparator (boolean ignoreStickyAnnounce) {
		this.ignoreStickyAnnounce = ignoreStickyAnnounce;
	}

	@Override public int compare(final Topic topic1, final Topic topic2)
	{
		int result;
		int type1 = topic1.getType(), type2 = topic2.getType();
		// Wiki pages are not sorted especially
		if (type1 == Topic.TYPE_WIKI) {
			type1 = Topic.TYPE_NORMAL;
		}
		if (type2 == Topic.TYPE_WIKI) {
			type2 = Topic.TYPE_NORMAL;
		}
		if (ignoreStickyAnnounce) {
			type1 = Topic.TYPE_NORMAL;
			type2 = Topic.TYPE_NORMAL;
		}

		if (type1 < type2) {
			result = 1;
		}
		else if (type1 == type2) {
			Date dt1 = topic1.getLastPostDate();
			if (topic1.getLastEditTime() != null) {
				dt1 = topic1.getLastEditTime();
			}
			Date dt2 = topic2.getLastPostDate();
			if (topic2.getLastEditTime() != null) {
				dt2 = topic2.getLastEditTime();
			}

			result = dt2.compareTo(dt1);
		} 
		else {
			result = -1;
		}
		
		return result;
	}

}

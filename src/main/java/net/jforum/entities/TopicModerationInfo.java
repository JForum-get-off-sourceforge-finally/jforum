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
 * Created on Jan 30, 2005 5:15:36 PM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafael Steil
 */
public class TopicModerationInfo
{
	private int topicId;
	private int topicReplies;
	private String topicTitle;
	private transient final List<Post> posts = new ArrayList<>();
	
	public void setTopicId(final int topicId)
	{
		this.topicId = topicId;
	}
	
	public void setTopicReplies(final int replies)
	{
		this.topicReplies = replies;
	}
	
	public void setTopicTitle(final String title)
	{
		this.topicTitle = title;
	}
	
	public int getTopicId()
	{
		return this.topicId;
	}
	
	public String getTopicTitle()
	{
		return this.topicTitle;
	}
	
	public void addPost(final Post post)
	{
		this.posts.add(post);
	}
	
	public List<Post> getPosts()
	{
		return this.posts;
	}
	
	public int getTopicReplies()
	{
		return this.topicReplies;
	}
}

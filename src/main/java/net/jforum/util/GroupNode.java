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
 * Created on Mar 3, 2003 / 11:29:45 AM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.util.ArrayList;
import java.util.List;

/** 
 * Represents a node in the group hierarchy.
 * Every single group has a name and an ID, and each group may have 
 * unlimited subgroups. which may have sub-subgroups and so on.
 * This class represents a specific group, supplying methods to add new
 * groups and get information about them, as well all related subgroups.
 * <p>
 * This class is also used together with <code>TreeGroup</code>.
 * 
 * @author Rafael Steil
 */
public class GroupNode 
{
	private String name;
	private int id;
	
	/**
	 * Subgroups of the instantiated group
	 */
	private final List<GroupNode> extraNodes;

	/**
	 * Default Constructor
	 */
	public GroupNode()
	{
		this.extraNodes = new ArrayList<>();
	}

	/**
	 * Gets a node.
	 * 
	 * @param pos Node position to retrieve
	 * @return <code>GroupNode</code>
	 * */
	public GroupNode getNode(final int pos)
	{
		return this.extraNodes.get(pos);
	}

	/**
	 * Adds a new node.
	 * 
	 * @param groupNode Node to add
	 * */
	public void addNode(final GroupNode groupNode)
	{
		this.extraNodes.add(groupNode);
	}

	/**
	 * Gets the total number of nodes 
	 * 
	 * @return number of nodes. 
	 * */
	public int size()
	{
		return this.extraNodes.size();
	}

	/**
	 * Sets the node's name
	 * 
	 * @param name Node name
	 * */
	public void setName(final String name)
	{
		this.name = name;
	}

	/**
	 * Sets the node's ID
	 * 
	 * @param id Node ID
	 * */
	public void setId(final int id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of the node
	 * 
	 * @return String with the name
	 * */	
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Gets the ID
	 * 
	 * @return Node ID
	 * */
	public int getId()
	{
		return this.id;
	}
}

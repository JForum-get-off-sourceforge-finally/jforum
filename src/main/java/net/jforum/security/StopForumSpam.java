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
 * Created on Oct 16, 2011 / 12:04:35 AM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * StopForumSpam 
 * @author Andowson Chang
 *
 */
public class StopForumSpam {
	private static final String baseURL = "http://www.stopforumspam.com/api?";
	
	public static boolean checkIp(String ip) {
		boolean result = false;
		String url = baseURL + "ip=" + ip;
		Element root = getXmlRootElement(url);
		String appears = root.getChildTextTrim("appears");
		if ("yes".equals(appears)) {
			result = true;
		}
		return result;
	}
	
	public static boolean checkEmail(String email) {
		boolean result = false;
		String url = baseURL + "email=" + email;
		Element root = getXmlRootElement(url);
		String appears = root.getChildTextTrim("appears");
		if ("yes".equals(appears)) {
			result = true;
		}
		return result;
	}
	
	public static void listChildren(Element current, int depth) {
		printSpaces(depth);
		System.out.println(current.getName());
		List children = current.getChildren();
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			Element child = (Element) iterator.next();
			listChildren(child, depth + 1);
		}
	}

	private static void printSpaces(int n) {
		for (int i = 0; i < n; i++) {
			System.out.print(' ');
		}
	}
	
	public static Element getXmlRootElement(String url) {
		try {
			SAXBuilder xparser = new SAXBuilder();
			Document doc = xparser.build(url);
			Element root = doc.getRootElement();
			return root;
		} catch (JDOMException e) {
            // indicates a well-formedness error
			System.out.println("The result XML is not well-formed.");
			System.out.println(e.getMessage());
		} catch (IOException ioe) {
			System.out.println("Oh no!...IOException");
			System.out.println(ioe.getMessage());
		}
		return null;
	}
}

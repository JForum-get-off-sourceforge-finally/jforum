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
 * Created on 27/09/2004 23:59:10
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * Process text with html and remove possible malicious tags and attributes.
 * Work based on tips from Amit Klein and the following documents:
 * <br>
 * <li>http://ha.ckers.org/xss.html
 * <li>http://quickwired.com/kallahar/smallprojects/php_xss_filter_function.php
 * <br>
 * @author Rafael Steil
 */
public class SafeHtml 
{
	private static Set<String> welcomeTags;
	private static Set<String> welcomeAttributes;
	private static Set<String> allowedProtocols;
	private static String forumLink;
	private static Safelist safe;

	static {
		welcomeTags = new HashSet<>();
		welcomeAttributes = new HashSet<>();
		allowedProtocols = new HashSet<>();

		updateConfiguration();
	}

	public static void updateConfiguration() {
		welcomeTags.clear();
		welcomeAttributes.clear();
		allowedProtocols.clear();
		splitAndTrim(ConfigKeys.HTML_TAGS_WELCOME, welcomeTags);
		splitAndTrim(ConfigKeys.HTML_ATTRIBUTES_WELCOME, welcomeAttributes);
		splitAndTrim(ConfigKeys.HTML_LINKS_ALLOW_PROTOCOLS, allowedProtocols);

		forumLink = SystemGlobals.getValue(ConfigKeys.FORUM_LINK);

		safe = Safelist.none();
		safe.preserveRelativeLinks(SystemGlobals.getBoolValue(ConfigKeys.HTML_LINKS_ALLOW_RELATIVE));

		for (String tag : welcomeTags) {
			safe.addTags(tag);
		}

		for (String attr : welcomeAttributes) {
			safe.addAttributes(":all", attr);
		}
		safe.addAttributes(":all", "class", "id");
		safe.addAttributes("a", "rel", "target", "href");
		safe.addAttributes("img", "src", "border", "alt", "width", "height");

		for (String protocol : allowedProtocols) {
			if (protocol.contains(":")) {
				// previously, protocols included colon and slashes
				protocol = protocol.substring(0, protocol.indexOf(":"));
			}
			safe.addProtocols("a", "href", protocol.toLowerCase());
			safe.addProtocols("img", "src", protocol.toLowerCase());
		}
	}

	private static void splitAndTrim (String s, Set<String> data)
	{
		String value = SystemGlobals.getValue(s);

		if (value == null) {
			return;
		}

		String[] tags = value.split(",");

		for (int i = 0; i < tags.length; i++) {
			data.add(tags[i].trim());
		}
	}

	/**
	 * Make sure that attribute values are safe, based on name and value
	 */
	public static String ensureAllAttributesAreSafe (String contents) 
	{
		Document doc = Jsoup.parseBodyFragment(contents);
		Elements el = doc.getAllElements();
		for (Element e : el) {
			List<String>  attToRemove = new ArrayList<>();
			Attributes at = e.attributes();
			for (Attribute a : at) {
				String name = a.getKey().toLowerCase();
				String value = a.getValue().toLowerCase();
				if (name.length() >= 2 && name.charAt(0) == 'o' && name.charAt(1) == 'n') {
					// disallow JavaScript onXYZ handlers
					attToRemove.add(name);
				}
				else if ("style".equals(name)) {
					// It is much more a try to not allow constructions
					// like style="background-color: url(javascript:xxxx)" than anything else
					if (value.indexOf('(') > -1) {
						attToRemove.add(name);
					}
				}
				else if ("href".equals(name) || "src".equals(name)) {
					if (! isHrefValid(value)) {
						attToRemove.add(name);
					}
				}
				else {
					// remove quotes, so it can't escape its value
					a.setValue(value.replaceAll("\n\r'\"", ""));
				}
			}

			for (String att : attToRemove) {
				e.removeAttr(att);
			}
		}

		return doc.body().html();
	}

	/**
	 * Checks if a given address is valid
	 * @param href The address to check
	 * @return true if it is valid
	 */
	private static boolean isHrefValid (String href) 
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.HTML_LINKS_ALLOW_RELATIVE)
			&& href.length() > 0 
			&& href.charAt(0) == '/') {
			return true;
		}
		
		for (String protocol : allowedProtocols) {
			if (href.startsWith(protocol)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Given an input, makes it safe for HTML displaying. 
	 * Removes any forbidden HTML tags or attributes, as well as unwanted JavaScript statements inside the tags. 
	 * @param contents the input to analyze
	 * @return the modified and safe string
	 */
	public static String makeSafe (String contents)
	{
		if (contents == null || contents.length() == 0) {
			return contents;
		}

		return Jsoup.clean(contents, forumLink, safe, new Document.OutputSettings().prettyPrint(false));
	}

	public static String replaceAll (StringBuilder sb, String what, String with)
	{
		int pos = sb.indexOf(what);

		while (pos > -1) {
			sb.replace(pos, pos + what.length(), with);
			pos = sb.indexOf(what);
		}

		return sb.toString();
	}
}

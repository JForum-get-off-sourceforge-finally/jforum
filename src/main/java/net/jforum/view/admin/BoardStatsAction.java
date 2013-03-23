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
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
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
 * Created on 23/07/2007 15:14:27
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.view.admin;

import java.io.*;
import java.net.*;
import java.util.*;

import net.jforum.util.preferences.*;
import net.jforum.view.forum.common.*;

public class BoardStatsAction extends AdminCommand {
    /**
     * @see net.jforum.Command#list()
     */
    public void list() {
        this.setTemplateName(TemplateKeys.BOARD_STATS_LIST);
        this.context.put("records", Stats.getRecords());
        this.context.put("restartTime", Stats.getRestartTime());

		List<Item> sysInfo = new ArrayList<Item>();
        sysInfo.add(new Item("Java version", System.getProperty("java.version")));
        sysInfo.add(new Item("Max memory", ""+Runtime.getRuntime().maxMemory()));
        sysInfo.add(new Item("Total memory", ""+Runtime.getRuntime().totalMemory()));
        sysInfo.add(new Item("Free memory", ""+Runtime.getRuntime().freeMemory()));
        Collections.sort(sysInfo);
        this.context.put("sysInfo", sysInfo);
    }

    public void showLast() {
        this.setTemplateName(TemplateKeys.BOARD_STATS_SHOW_LAST);
        String tag = this.request.getParameter("tag");
        try {
            tag = URLDecoder.decode(tag, "UTF-8");
            Map<Date, Object> values = new HashMap();
            if (tag != null && !Stats.ForbidDetailDisplay.isForbidden(tag)) {
                tag = URLDecoder.decode(tag, "UTF-8");
                Stats.Data data = Stats.getStatsFor(tag);
                values = data.getValues();
            }
			List<Date> times = new ArrayList<Date>(values.keySet());
			// sort list of descending time
			Collections.sort(times, new Comparator<Date>() {
				@Override
				public int compare (Date obj1, Date obj2) {
					if (obj1.getTime() < obj2.getTime())	return 1;
					if (obj1.getTime() > obj2.getTime())	return -1;
					else 									return 0;
				}
				@Override
				public boolean equals (Object dt) { return false; }
			});
            this.context.put("tag", tag);
            this.context.put("times", times);     
            this.context.put("data", values);     
        } catch (UnsupportedEncodingException e) {
            // Whatever
        }
    }

    public static class Item implements Comparable {
        private String name, value;

		Item (String name, String value) {
			this.name = name;
			this.value = value;
		}

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

		public int compareTo (Object rec) {
			return name.compareTo(((Item) rec).name);
		}
    }
}

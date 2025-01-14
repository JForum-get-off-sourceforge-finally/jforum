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
 * The JForum Project
 * http://www.jforum.net
 */

package net.jforum.util.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import net.jforum.util.BoundedLinkedHashMap;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

/**
 * Collect generic performance statistics. Stores timestamped records in a ring buffer.
 */
public class Stats {
    
	private static Stats INSTANCE = new Stats();
    private Map<String, Data> buffers = new ConcurrentHashMap<>();
    private Date restartTime = new Date();

	public static void init() {
		EventBus.getDefault().register(INSTANCE);
	}
	
	public static void stop() {
		for (Map.Entry<String, Data> entry : INSTANCE.buffers.entrySet()) {
			entry.getValue().buffer.clear();
		}
		INSTANCE.buffers.clear();
		
		EventBus.getDefault().unregister(INSTANCE);
	}

    public static Data getStatsFor(String tag) {
		INSTANCE.buffers.putIfAbsent(tag, new Data());
        return INSTANCE.buffers.get(tag);
    }

	/* called by EventBus */
	@Subscribe
	public void onEvent(StatsEvent event) {
        if (active()) {
            getStatsFor(event.getTag()).record(event.getData());
        }
	}

    public static boolean active() {
        return SystemGlobals.getBoolValue(ConfigKeys.STATS_ACTIVE);
    }

    public static List<Record> getRecords() {
        List<Record> result = new ArrayList<Record>();
        for (Map.Entry<String, Data> entry : INSTANCE.buffers.entrySet()) {
            Record r = new Record();
            r.tag = entry.getKey();
            Data data = entry.getValue();
            r.count = data.count;
            if (data.buffer.size() > 0) {
                long first = data.buffer.keySet().iterator().next().getTime();
                long interval = System.currentTimeMillis() - first;
                r.cps = data.buffer.size() / ((double) interval) * 60000.;
            }
			result.add(r);
		}
		result.sort(null);
        return result;
    }

    public static Date getRestartTime() {
        return INSTANCE.restartTime;
    }

    public static class Record implements Comparable<Object> {
        private String tag;
        private double cps;
        private long count;

        public String getTag() {
            return tag;
        }

        public double getCps() {
            return cps;
        }

        public long getCount() {
            return count;
        }
        
        public boolean isDetailForbidden() {
            return ForbidDetailDisplay.isForbidden(tag);
        }

		@Override public int compareTo (Object rec) {
			return tag.compareTo(((Record) rec).tag);
		}

		@Override public boolean equals (Object rec) {
			if ((rec == null) || ! (rec instanceof Record))
				return false;

			if (rec == this) return true;

			return 0 == ((Record) rec).compareTo(this);
		}

		@Override public int hashCode() {
			return Objects.hash( tag, Double.valueOf(cps), Long.valueOf(count) );
		}
    }

    public static class Data {
        private long count;
        private Map<Date, Object> buffer;
        private static final String LINK = "<a href='%s'>%s</a>";

        private Data() {
            int size = SystemGlobals.getIntValue(ConfigKeys.STATS_RING_SIZE);
            buffer = Collections.synchronizedMap(new BoundedLinkedHashMap<Date, Object>(size));
        }

        public long getCount() {
            return count;
        }

        void record(Object datum) {
            if (datum != null && datum.toString().startsWith("http")) {
                datum = String.format(LINK, datum, datum);
            }
            buffer.put(new Date(), datum);
            ++count;
        }

        public Map<Date, Object> getValues() {
            return buffer;
        }
    }
    
    public static enum ForbidDetailDisplay {
        SENT_PMS ("Sent private message");

        private final String value;

        private ForbidDetailDisplay (String value) {
            this.value = value;
        }

        @Override public String toString() {
            return value;
        }

        public static boolean isForbidden (String candidate) {
            for (ForbidDetailDisplay forbidden : ForbidDetailDisplay.values()) {
                if ( forbidden.toString().equals(candidate)) {
                    return true;
                }
            }
            return false;
        }
    }
}

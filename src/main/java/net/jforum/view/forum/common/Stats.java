package net.jforum.view.forum.common;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import net.jforum.util.BoundedLinkedHashMap;
import net.jforum.util.preferences.*;

import org.apache.commons.lang3.StringUtils;

/**
 * Collect generic performance statistics. Stores timestamped records in a ring buffer.
 */
public class Stats {
    
    private static Map<String, Data> buffers = new ConcurrentHashMap<String, Data>();
    private static Date restartTime = new Date();

    private Stats() {/* Uninstantiable class */
    }

    public static Data getStatsFor(String tag) {
        Data buffer = buffers.get(tag);
        if (buffer == null) {
            buffer = new Data();
            buffers.put(tag, buffer);
        }
        return buffer;
    }

    public static void record(String tag, Object data) {
        if (active()) {
            getStatsFor(tag).record(data);
        }
    }

    public static boolean active() {
        return SystemGlobals.getBoolValue(ConfigKeys.STATS_ACTIVE);
    }


    public static List<Record> getRecords() {
        List<Record> result = new ArrayList<Record>();
        for (Map.Entry<String, Data> entry : buffers.entrySet()) {
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
		Collections.sort(result);
        return result;
    }

    private static String dumpStackTrace(Exception e) {
        StringWriter temp = new StringWriter();
        e.printStackTrace(new PrintWriter(temp));
        return temp.toString();
    }

    private static void close(InputStream in) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
    
    private static void close(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static Date getRestartTime() {
        return restartTime;
    }

    public static class Record implements Comparable {
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

		public int compareTo (Object rec) {
			return tag.compareTo(((Record) rec).tag);
		}
    }

    public static class Data {
        private long count;
        private Map<Date, Object> buffer;
        private static final String LINK = "<a href='%s'>%s</a>";

        private Data() {
            int size = SystemGlobals.getIntValue(ConfigKeys.STATS_RING_SIZE);
            buffer = Collections.synchronizedMap(new BoundedLinkedHashMap(size));
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
            //return buffer.values();
        }
    }

    public static class ServerMetric {
        private String tag;
        private String value;

        public String getTag() {
            return tag;
        }

        public String getValue() {
            return value;
        }
    }
    
    public static enum ForbidDetailDisplay {
        SENT_PMS ( "Sent private message");
        
        private String value;
        private ForbidDetailDisplay(String _value) {
            value = _value;
        }
        public String toString() {
            return value;
        }
        public static boolean isForbidden(String candidate) {
            for (ForbidDetailDisplay forbidden : ForbidDetailDisplay.values()) {
                if ( forbidden.toString().equals(candidate)) {
                    return true;
                }
            }
            return false;
        }
    }
}

package net.jforum.util.legacy.clickstream.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Clickstream configuration data.
 *
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 * @author Rafael Steil (little hacks for JForum)
 * @version $Id: ClickstreamConfig.java,v 1.4 2005/07/26 04:01:16 diegopires Exp $
 */
public class ClickstreamConfig {
    private List<String> botAgents = new ArrayList<String>();
    private List<String> botHosts = new ArrayList<String>();

    public void addBotAgent(String agent) {
        botAgents.add(agent);
    }

    public void addBotHost(String host) {
        botHosts.add(host);
    }

    public List<String> getBotAgents() {
        return botAgents;
    }

    public List<String> getBotHosts() {
        return botHosts;
    }
}

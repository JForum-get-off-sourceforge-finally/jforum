/*
 * Created on 21/08/2006 22:00:12
 */
package net.jforum.api.integration.mail.pop;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 * @version $Id: POPParser.java,v 1.3 2006/10/05 02:00:23 rafaelsteil Exp $
 */
public class POPParser
{
	private static final Logger LOGGER = Logger.getLogger(POPParser.class);
	
	private transient final List<POPMessage> messages = new ArrayList<POPMessage>();
	
	public void parseMessages(final POPConnector connector)
	{
		final Message[] connectorMessages = connector.listMessages();
		
		for (int i = 0; i < connectorMessages.length; i++) {
			final POPMessage message = new POPMessage(connectorMessages[i]);
			this.messages.add(message);
			
			LOGGER.debug("Retrieved message " + message);
		}
	}
	
	public List<POPMessage> getMessages()
	{
		return this.messages;
	}
}

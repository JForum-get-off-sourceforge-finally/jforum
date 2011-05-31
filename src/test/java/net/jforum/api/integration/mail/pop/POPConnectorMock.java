/*
 * Created on 30/08/2006 22:09:25
 */
package net.jforum.api.integration.mail.pop;

import javax.mail.Message;

/**
 * @author Rafael Steil
 * @version $Id$
 */
public class POPConnectorMock extends POPConnector
{
	private Message[] messages;
	
	public void setMessages(Message[] messages)
	{
		this.messages = messages;
	}
	
	public Message[] listMessages()
	{
		return this.messages;
	}
	
	public void openConnection() {}
	public void closeConnection() {}
} 

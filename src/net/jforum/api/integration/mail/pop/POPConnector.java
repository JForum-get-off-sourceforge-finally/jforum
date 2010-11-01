/*
 * Created on 21/08/2006 21:08:19
 */
package net.jforum.api.integration.mail.pop;

import java.util.Arrays;
import java.util.Properties;

import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import net.jforum.entities.MailIntegration;
import net.jforum.exceptions.MailException;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

/**
 * Handles the connection to the POP server.
 * 
 * @author Rafael Steil
 * @version $Id: POPConnector.java,v 1.4 2006/10/05 02:00:23 rafaelsteil Exp $
 */
public class POPConnector
{
	private static final Logger LOGGER = Logger.getLogger(POPConnector.class);
	
	private transient Store store;
	private transient Folder folder;
	private transient MailIntegration mailIntegration;
	private transient Message[] messages;
	
	/**
	 * @param mailIntegration the {@link MailIntegration} instance with 
	 * all the information necessary to connect to the pop server
	 */
	public void setMailIntegration(final MailIntegration mailIntegration)
	{
		this.mailIntegration = mailIntegration;
	}
	
	/**
	 * Lists all available messages in the pop server
	 * @return Array of {@link Message}'s
	 */
	public Message[] listMessages()
	{
		try {
			this.messages = this.folder.getMessages();
			return Arrays.copyOf(this.messages, this.messages.length);
		}
		catch (Exception e) {
			throw new MailException(e);
		}
	}
	
	/**
	 * Opens a connection to the pop server. 
	 * The method will try to retrieve the <i>INBOX</i> folder in 
	 * <i>READ_WRITE</i> mode
	 */
	public void openConnection()
	{
		try {
			final Session session = Session.getDefaultInstance(new Properties());
			
			this.store = session.getStore(this.mailIntegration.isSsl() ? "pop3s" : "pop3");

			this.store.connect(this.mailIntegration.getPopHost(), 
					this.mailIntegration.getPopPort(), 
					this.mailIntegration.getPopUsername(),
					this.mailIntegration.getPopPassword());
			
			this.folder = this.store.getFolder("INBOX");
			
			if (folder == null) {
				throw new Exception("No Inbox");
			}
			
			this.folder.open(Folder.READ_WRITE);
		}
		catch (Exception e) {
			throw new MailException(e);
		}
	}
	
	/**
	 * Closes the connection to the pop server.
	 * Before finishing the communication channel, all messages
	 * are flagged for deletion.
	 */
	public void closeConnection()
	{
		final boolean deleteMessages = !SystemGlobals.getBoolValue(ConfigKeys.MAIL_POP3_DEBUG_KEEP_MESSAGES);
		this.closeConnection(deleteMessages);
	}
	
	/**
	 * Closes the connection to the pop server.
	 * @param deleteAll If true, all messages are flagged for deletion
	 */
	public void closeConnection(final boolean deleteAll)
	{
		if (deleteAll) {
			this.markAllMessagesAsDeleted();
		}
		
		if (this.folder != null) {
			try {
				this.folder.close(false);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if (this.store != null) {
			try {
				this.store.close();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Flag all messages for deletion.
	 */
	private void markAllMessagesAsDeleted()
	{
		try {
			if (this.messages != null) {
				for (int i = 0; i < this.messages.length; i++) {
					this.messages[i].setFlag(Flag.DELETED, true);
				}
			}
		}
		catch (Exception e) {
			throw new MailException(e);
		}
	}
}

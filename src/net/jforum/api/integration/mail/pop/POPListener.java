/*
 * Created on 21/08/2006 21:07:36
 */
package net.jforum.api.integration.mail.pop;

import java.util.Iterator;
import java.util.List;

import net.jforum.dao.DataAccessDriver;
import net.jforum.entities.MailIntegration;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Rafael Steil
 * @version $Id: POPListener.java,v 1.9 2006/10/10 01:59:55 rafaelsteil Exp $
 */
public class POPListener implements Job
{
	private static final Logger LOGGER = Logger.getLogger(POPListener.class);
	private static boolean working = false;
	protected POPConnector connector = new POPConnector();
	
	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(final JobExecutionContext jobContext) throws JobExecutionException
	{
		if (working) {
			LOGGER.debug("Already working. Leaving for now.");
		} else {	
			try {
				working = true;

				final List<MailIntegration> integrationList = DataAccessDriver.getInstance().newMailIntegrationDAO().findAll();
				final POPParser parser = new POPParser();
				
				for (final Iterator<MailIntegration> iter = integrationList.iterator(); iter.hasNext(); ) {
					final MailIntegration integration = iter.next();
					
					connector.setMailIntegration(integration);
					
					try {
						LOGGER.debug("Going to check " + integration);
						
						connector.openConnection();
						parser.parseMessages(connector);
						
						final POPPostAction postAction = new POPPostAction();
						postAction.insertMessages(parser);
					}
					finally {
						connector.closeConnection();
					}
				}
			}
			finally {
				working = false;
			}
		}		
	}
	
	public POPConnector getConnector()
	{
		return this.connector;
	}
}

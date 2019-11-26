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
 * Created on 21/08/2006 21:07:36
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.api.integration.mail.pop;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.jforum.dao.DataAccessDriver;
import net.jforum.entities.MailIntegration;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.apache.log4j.Logger;

/**
 * @author Rafael Steil
 */
public class POPListener implements Job
{
	private static final Logger LOGGER = Logger.getLogger(POPListener.class);
	private static final Lock lock = new ReentrantLock();
	protected transient POPConnector connector = new POPConnector();
	
	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override public void execute(final JobExecutionContext jobContext) throws JobExecutionException
	{
		if (! lock.tryLock()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Already working. Leaving for now.");
			}
			return;
		}

		try {
			final List<MailIntegration> integrationList = DataAccessDriver.getInstance().newMailIntegrationDAO().findAll();
			final POPParser parser = new POPParser();

			for (final Iterator<MailIntegration> iter = integrationList.iterator(); iter.hasNext(); ) {
				final MailIntegration integration = iter.next();

				connector.setMailIntegration(integration);

				try {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Going to check " + integration);
					}

					connector.openConnection();
					parser.parseMessages(connector);

					final POPPostAction postAction = new POPPostAction();
					postAction.insertMessages(parser);
				}
				finally {
					connector.closeConnection();
				}
			}
		} finally {
			lock.unlock();
		}
	}

	public POPConnector getConnector()
	{
		return this.connector;
	}
}

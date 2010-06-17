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
 * This file creation date: Mar 3, 2003 / 11:43:35 AM
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.jforum.context.JForumContext;
import net.jforum.context.RequestContext;
import net.jforum.context.ResponseContext;
import net.jforum.context.web.WebRequestContext;
import net.jforum.context.web.WebResponseContext;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.MySQLVersionWorkarounder;
import net.jforum.dao.SqlServerVersionWorkarounder;
import net.jforum.dao.UserDAO;
import net.jforum.entities.Banlist;
import net.jforum.entities.User;
import net.jforum.entities.UserSession;
import net.jforum.exceptions.ExceptionWriter;
import net.jforum.exceptions.ForumStartupException;
import net.jforum.repository.BanlistRepository;
import net.jforum.repository.ModulesRepository;
import net.jforum.repository.RankingRepository;
import net.jforum.repository.SecurityRepository;
import net.jforum.repository.SmiliesRepository;
import net.jforum.util.FileMonitor;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.log4j.Logger;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Front Controller.
 * 
 * @author Rafael Steil
 * @version $Id: JForum.java,v 1.116 2007/10/10 04:54:20 rafaelsteil Exp $
 */
public class JForum extends JForumBaseServlet 
{
	private static final Logger LOGGER = Logger.getLogger(JForum.class);
	
	private static final long serialVersionUID = 7160936607198716279L;
	private static boolean isDatabaseUp;
	
	/**
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(final ServletConfig config) throws ServletException
	{
		super.init(config);
		super.startApplication();
		
		final String containerInfo = config.getServletContext().getServerInfo();		
		LOGGER.info("Container: " + containerInfo);
		final String[] info = containerInfo.split("/");
		SystemGlobals.setValue("container.app", info[0]);
		SystemGlobals.setValue("container.version", String.valueOf(info[1].charAt(0)));		
		
		// Start database
		JForum.setDatabaseUp(ForumStartup.startDatabase());
		
		try {
			final Connection conn = DBConnection.getImplementation().getConnection();
			conn.setAutoCommit(!SystemGlobals.getBoolValue(ConfigKeys.DATABASE_USE_TRANSACTIONS));
			
			// Try to fix some MySQL problems
	    	if ("mysql".equals(SystemGlobals.getValue(ConfigKeys.DATABASE_DRIVER_NAME))) {
	    		final MySQLVersionWorkarounder dbWorkarounder = new MySQLVersionWorkarounder();
				dbWorkarounder.handleWorkarounds(conn);	
	    	}			
			
			// Try to fix some SQL Server problems
	    	if ("sqlserver".equals(SystemGlobals.getValue(ConfigKeys.DATABASE_DRIVER_NAME))) {
	    		final SqlServerVersionWorkarounder dbWorkarounder2 = new SqlServerVersionWorkarounder();
				dbWorkarounder2.handleWorkarounds(conn);
	    	}
	    	
			// Continues loading the forum
			JForumExecutionContext executionContext = JForumExecutionContext.get();
			executionContext.setConnection(conn);
			JForumExecutionContext.set(executionContext);
			
			// Init general forum stuff
			ForumStartup.startForumRepository();
			RankingRepository.loadRanks();
			SmiliesRepository.loadSmilies();
			BanlistRepository.loadBanlist();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
            throw new ForumStartupException("Error while starting jforum", e);
		}
		finally {
			JForumExecutionContext.finish();
		}
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void service(final HttpServletRequest req, final HttpServletResponse res) throws IOException, ServletException
	{
		Writer out = null;
		JForumContext forumContext = null;
		RequestContext request = null;
		ResponseContext response = null;
		String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);

		try {
			// Initializes the execution context
			JForumExecutionContext executionContext = JForumExecutionContext.get();

			request = new WebRequestContext(req);
            response = new WebResponseContext(res);

			this.checkDatabaseStatus();

            forumContext = new JForumContext(request.getContextPath(),
                SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION),
                request,
                response
            );
            executionContext.setForumContext(forumContext);

            JForumExecutionContext.set(executionContext);

            // Setup stuff
			SimpleHash context = JForumExecutionContext.getTemplateContext();
			
			ControllerUtils utils = new ControllerUtils();
			utils.refreshSession();
			
			context.put("logged", SessionFacade.isLogged());
			
			// Process security data
			SecurityRepository.load(SessionFacade.getUserSession().getUserId());

			utils.prepareTemplateContext(context, forumContext);

			String module = request.getModule();
			
			// Gets the module class name
			String moduleClass = module != null 
				? ModulesRepository.getModuleClass(module) 
				: null;
			
			if (moduleClass == null) {
				// Module not found, send 404 not found response
				//response.sendError(HttpServletResponse.SC_NOT_FOUND);
				response.sendRedirect(request.getContextPath());
			}
			else {
				boolean shouldBan = this.shouldBan(request.getRemoteAddr());
				
				if (shouldBan) {
					moduleClass = ModulesRepository.getModuleClass("forums");
					context.put("moduleName", "forums");
					((WebRequestContext)request).changeAction("banned");					
				}
				else {
					context.put("moduleName", module);
					context.put("action", request.getAction());
				}
				
				if (shouldBan && SystemGlobals.getBoolValue(ConfigKeys.BANLIST_SEND_403FORBIDDEN)) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
				}
				else {
					context.put("language", I18n.getUserLanguage());
					context.put("session", SessionFacade.getUserSession());
					context.put("request", req);
					context.put("response", response);
					
					out = this.processCommand(out, request, response, encoding, context, moduleClass);
				}
			}
		}
		catch (Exception e) {
			this.handleException(out, response, encoding, e, request);
		}
		finally {
			this.handleFinally(out, forumContext, response);
		}		
	}

	private Writer processCommand(Writer out, final RequestContext request, final ResponseContext response, 
			final String encoding, final SimpleHash context, final String moduleClass) throws IOException, TemplateException, InstantiationException, IllegalAccessException, ClassNotFoundException 
	{
		// Here we go, baby
		Command command = this.retrieveCommand(moduleClass);
		Template template = command.process(request, response, context);

		if (JForumExecutionContext.getRedirectTo() == null) {
			String contentType = JForumExecutionContext.getContentType();
			
			if (contentType == null) {
				contentType = "text/html; charset=" + encoding;
			}
			
			response.setContentType(contentType);
			
			// Binary content are expected to be fully 
			// handled in the action, including outputstream
			// manipulation
			if (!JForumExecutionContext.isCustomContent()) {
				out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), encoding));
				template.process(JForumExecutionContext.getTemplateContext(), out);
				out.flush();
			}
		}
		
		return out;
	}

	private void checkDatabaseStatus()
	{
		if (!JForum.isDatabaseUp()) {
			synchronized (this) {
				if (!JForum.isDatabaseUp()) {
					JForum.setDatabaseUp(ForumStartup.startDatabase());
				}
			}
		}
	}

	private void handleFinally(Writer out, JForumContext forumContext, ResponseContext response) throws IOException
	{
		try {
			if (out != null) { out.close(); }
		}
		catch (Exception e) {
		    // catch close error 
		}
		
		String redirectTo = JForumExecutionContext.getRedirectTo();
		JForumExecutionContext.finish();
		
		if (redirectTo != null) {
			if (forumContext != null && forumContext.isEncodingDisabled()) {
				response.sendRedirect(redirectTo);
			} 
			else {
				response.sendRedirect(response.encodeRedirectURL(redirectTo));
			}
		}
	}

	private void handleException(Writer out, ResponseContext response, String encoding, 
		Exception exception, RequestContext request) throws IOException
	{
		JForumExecutionContext.enableRollback();
		
		if (exception.toString().indexOf("ClientAbortException") == -1) {
			response.setContentType("text/html; charset=" + encoding);
			if (out != null) {
				new ExceptionWriter().handleExceptionData(exception, out, request);
			}
			else {
				new ExceptionWriter().handleExceptionData(exception, new BufferedWriter(new OutputStreamWriter(response.getOutputStream())), request);
			}
		}
	}
	
	private boolean shouldBan(String ip)
	{
		Banlist b = new Banlist();
		
		UserDAO dao = DataAccessDriver.getInstance().newUserDAO();
		User user = dao.selectById(SessionFacade.getUserSession().getUserId());
		b.setUserId(user.getId());
		b.setEmail(user.getEmail());
		b.setIp(ip);
		
		return BanlistRepository.shouldBan(b);
	}

	private Command retrieveCommand(String moduleClass) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		return (Command)Class.forName(moduleClass).newInstance();
	}
	
	/** 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() 
	{
		super.destroy();
		LOGGER.info("Destroying JForum...");
		
		// stop FileMonitor threads
		LOGGER.debug("Close file monitors ...");
		closeFileMonitor();
		
		// invalidate all sessions to force SessionFacade.storeSessionData()		
		LOGGER.debug("Current sessions: " + SessionFacade.size());
		List<UserSession> sessions = SessionFacade.getAllSessions();
		for (Iterator<UserSession> iter = sessions.iterator(); iter.hasNext(); ) {
			UserSession us = (UserSession)iter.next();
			HttpSession session = (HttpSession)getServletContext().getAttribute(us.getSessionId());
			session.invalidate();
			LOGGER.debug("Current sessions: " + SessionFacade.size());
		}
	
		try {			
			ConfigLoader.stopCacheEngine();
			DBConnection.getImplementation().realReleaseAllConnections();			
		}
		catch (Exception e) { LOGGER.error(e.getMessage(), e); }
	}

	private static boolean isDatabaseUp() 
	{
		return isDatabaseUp;
	}

	private static void setDatabaseUp(boolean isDatabaseUp) 
	{
		JForum.isDatabaseUp = isDatabaseUp;
	}
	
	private void closeFileMonitor()
	{
		FileMonitor.getInstance().removeFileChangeListener(SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_GENERIC));
		FileMonitor.getInstance().removeFileChangeListener(SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_DRIVER));
		FileMonitor.getInstance().removeFileChangeListener(SystemGlobals.getValue(ConfigKeys.DEFAULT_CONFIG));
		FileMonitor.getInstance().removeFileChangeListener(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
		FileMonitor.getInstance().removeFileChangeListener(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
		String baseDir = I18n.getBaseDir();
		Properties localeNames = I18n.getLocaleNames();
		FileMonitor.getInstance().removeFileChangeListener(baseDir + localeNames.getProperty(SystemGlobals.getValue(ConfigKeys.I18N_DEFAULT_ADMIN)));
		FileMonitor.getInstance().removeFileChangeListener(baseDir + localeNames.getProperty(SystemGlobals.getValue(ConfigKeys.I18N_DEFAULT)));
	}
}

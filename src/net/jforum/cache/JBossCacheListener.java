/*
 * Created on Mar 15, 2005 1:22:52 PM
 */
package net.jforum.cache;

import org.apache.log4j.Logger;
import org.jboss.cache.Cache;
import org.jboss.cache.Fqn;
import org.jboss.cache.notifications.annotation.CacheListener;
import org.jboss.cache.notifications.annotation.CacheStarted;
import org.jboss.cache.notifications.annotation.CacheStopped;
import org.jboss.cache.notifications.annotation.NodeCreated;
import org.jboss.cache.notifications.annotation.NodeModified;
import org.jboss.cache.notifications.annotation.NodeMoved;
import org.jboss.cache.notifications.annotation.NodeRemoved;
import org.jboss.cache.notifications.annotation.NodeVisited;
import org.jboss.cache.notifications.event.Event;
import org.jboss.cache.notifications.event.NodeEvent;
import org.jgroups.View;

/**
 * @author Rafael Steil, Andowson Chang
 * @version $Id: JBossCacheListener.java,v 1.4 2007/08/01 22:30:06 rafaelsteil
 *          Exp $
 */
@CacheListener
public class JBossCacheListener {
	private static final Logger LOGGER = Logger.getLogger(JBossCacheListener.class);
	
	@CacheStarted
	@CacheStopped
	public void cacheStartStopEvent(final Event event) {
		switch (event.getType())
		{
		case CACHE_STARTED:
			LOGGER.info("Cache has started");
			break;
		case CACHE_STOPPED:
			LOGGER.info("Cache has stopped");
			break;
		default:
			break;
		}
	}

	@NodeCreated
	@NodeRemoved
	@NodeVisited
	@NodeModified
	@NodeMoved
	public void logNodeEvent(final NodeEvent nodeEvent)
	{
		LOGGER.debug("An event on node " + nodeEvent.getFqn() + " has occured: " + nodeEvent.getType());
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#nodeCreated(org.jboss.cache.Fqn)
	 */
	public void nodeCreated(final Fqn<?> fqn) {
		// Empty method
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#nodeRemoved(org.jboss.cache.Fqn)
	 */
	public void nodeRemoved(final Fqn<?> fqn) {
		// Empty method
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#nodeLoaded(org.jboss.cache.Fqn)
	 */
	public void nodeLoaded(final Fqn<?> fqn) {
		// Empty method
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#nodeEvicted(org.jboss.cache.Fqn)
	 */
	public void nodeEvicted(final Fqn<?> fqn) {
		// Empty method
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#nodeModified(org.jboss.cache.Fqn)
	 */
	public void nodeModified(final Fqn<?> fqn) {
		// Empty method
		// if (CacheEngine.NOTIFICATION.startsWith((String)fqn.get(0))) {
		// }
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#nodeVisited(org.jboss.cache.Fqn)
	 */
	public void nodeVisited(final Fqn<?> fqn) {
		// Empty method
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#cacheStarted(org.jboss.cache.TreeCache)
	 */
	public void cacheStarted(final Cache<?, ?> cache) {
		// Empty method
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#cacheStopped(org.jboss.cache.TreeCache)
	 */
	public void cacheStopped(final Cache<?, ?> cache) {
		// Empty method
	}

	/**
	 * @see org.jboss.cache.TreeCacheListener#viewChange(org.jgroups.View)
	 */
	public void viewChange(final View view) {
		// Empty method
	}
}

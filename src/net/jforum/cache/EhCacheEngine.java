/******************************************************************************
 * Sony Online Entertainment
 * Application Engineering
 *
 * Unpublished work Copyright 2005 Sony Online Entertainment Inc.
 * All rights reserved.
 * Created on Oct 11, 2005
 ******************************************************************************/
package net.jforum.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.jforum.util.preferences.SystemGlobals;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
/**
 * The rest of the application seems to make some invalid assumptions about how
 * things are cached.  Those assumptions might be benign, but it is hard to tell
 * without deep testing.  Until this is finished the JBossCacheEngine should be 
 * configured in a local mode.
 *
 * Created on Oct 11, 2005 
 *
 * @author Jake Fear
 * @version $Id: EhCacheEngine.java,v 1.1 2005/10/14 00:15:54 rafaelsteil Exp $
 */
public class EhCacheEngine implements CacheEngine {

	private static final Logger LOGGER = Logger.getLogger(EhCacheEngine.class);
	
	private transient CacheManager manager;
	
	public void init() {
		try {
			manager = CacheManager.create(SystemGlobals.getValue("ehcache.cache.properties"));
		} catch (CacheException ce) {
			LOGGER.error("EhCache could not be initialized", ce);
			throw new RuntimeException(ce);
		}
	}

	public void stop() {
		LOGGER.info("stop()");
		manager.shutdown();
	}

	public void add(final String key, final Object value) {
		LOGGER.info("add("+key+", " + value +")");
		//if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Caching " + value + " with key " + key);
		//}
		add(DUMMY_FQN, key, value);
	}

	public void add(final String fqn, final String key, final Object value) {
		LOGGER.info("add("+fqn+", " + key +", " + value+")");
		/*if (!manager.cacheExists(fqn)) {
			try {
				manager.addCache(fqn);
			} catch (CacheException ce) {
				LOGGER.error(ce, ce);
				ce.printStackTrace();
				throw new RuntimeException(ce);
			}
		}*/
		try {
			if (!manager.cacheExists(fqn)) {
				LOGGER.info("cache "+ fqn +" doesn't exist, add one");
				manager.addCache(fqn);
			}
			final Cache cache = manager.getCache(fqn);
			final Element element = new Element(key, value);
			cache.put(element);
		} catch (Exception ce) {
			LOGGER.error(ce);
			throw new RuntimeException(ce);
		}
	}

	public Object get(final String fqn, final String key) {
		LOGGER.info("get("+fqn+", " + key +")");
		try {
			if (!manager.cacheExists(fqn)) {
				//manager.addCache(fqn);
				LOGGER.info("cache "+fqn+" doesn't exist and returns null");
				return null;
			}
			final Cache cache = manager.getCache(fqn);
			final Element element = cache.get(key);
			if (element != null) {
				LOGGER.info(key + "=" + element.getValue());
				return element.getValue();
			} 
			LOGGER.info("cache " + fqn + " exists but " + key + " returns null");
			return null;
		} catch (Exception ce) {
			LOGGER.error(ce);
			throw new RuntimeException(ce);
		}
	}

	public Object get(final String fqn) {
		LOGGER.info("get("+fqn+")");
		
		try {
			if (!manager.cacheExists(fqn)) {
				//manager.addCache(fqn);
				LOGGER.info("cache " + fqn + "not exists and return: null");
				return null;
			}
			final Cache cache = manager.getCache(fqn);
			return cache.getAllWithLoader(cache.getKeys(), null);
		} catch (Exception ce) {
			LOGGER.error("EhCache could not be shutdown", ce);
			throw new RuntimeException(ce);
		}
				
	}

	public Collection<Object> getValues(final String fqn) {
		LOGGER.info("getValues("+fqn+")");
		try {
			if (!manager.cacheExists(fqn)) {
				//manager.addCache(fqn);
				LOGGER.info("cache  " + fqn + "not exists and returns empty collection");
				return new ArrayList<Object>();
			}
			final Cache cache = manager.getCache(fqn);
			final List<Object> values = new ArrayList<Object>();
			final List<?> keys = cache.getKeys();
			
			for (final Iterator<?> iter = keys.iterator(); iter.hasNext(); ) {
				final Element element = cache.get(iter.next());
				if (element == null) {
					LOGGER.info("element is null");
				} else {					
					values.add(element.getValue());
				}
			}
			
			LOGGER.info("return:" + values);

			return values;
		} catch (Exception ce) {
			LOGGER.error("EhCache could not be shutdown", ce);
			throw new RuntimeException(ce);
		}
	}

	public void remove(final String fqn, final String key) {
		LOGGER.info("remove("+fqn+", " + key +")");
		try {
			final Cache cache = manager.getCache(fqn);

			if (cache != null) {
				cache.remove(key);
			}
		} catch (Exception ce) {
			LOGGER.error(ce);
			throw new RuntimeException(ce);
		}
	}

	public void remove(final String fqn) {
		LOGGER.info("remove("+fqn +")");
		try {
			if (manager.cacheExists(fqn)) {
				manager.removeCache(fqn);
			}
		} catch (Exception ce) {
			LOGGER.error(ce);
			throw new RuntimeException(ce);
		}
	}

}

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
	
	private CacheManager manager;
	
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

	public void add(String key, Object value) {
		LOGGER.info("add("+key+", " + value +")");
		//if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Caching " + value + " with key " + key);
		//}
		add(DUMMY_FQN, key, value);
	}

	public void add(String fullyQualifiedName, String key, Object value) {
		LOGGER.info("add("+fullyQualifiedName+", " + key +", " + value+")");
		/*if (!manager.cacheExists(fullyQualifiedName)) {
			try {
				manager.addCache(fullyQualifiedName);
			} catch (CacheException ce) {
				LOGGER.error(ce, ce);
				ce.printStackTrace();
				throw new RuntimeException(ce);
			}
		}*/
		try {
			if (!manager.cacheExists(fullyQualifiedName)) {
				LOGGER.info("cache "+ fullyQualifiedName +" doesn't exist, add one");
				manager.addCache(fullyQualifiedName);
			}
			Cache cache = manager.getCache(fullyQualifiedName);
			Element element = new Element(key, (Serializable)value);
			cache.put(element);
		} catch (Exception ce) {
			LOGGER.error(ce.getMessage(), ce);
			ce.printStackTrace();
			throw new RuntimeException(ce);
		}
	}

	public Object get(String fullyQualifiedName, String key) {
		LOGGER.info("get("+fullyQualifiedName+", " + key +")");
		try {
			if (!manager.cacheExists(fullyQualifiedName)) {
				//manager.addCache(fullyQualifiedName);
				LOGGER.info("cache "+fullyQualifiedName+" doesn't exist and returns null");
				return null;
			}
			Cache cache = manager.getCache(fullyQualifiedName);
			Element element = cache.get(key);
			if (element != null) {
				LOGGER.info(key + "=" + element.getValue());
				return element.getValue();
			} 
			LOGGER.info("cache "+fullyQualifiedName+" exists but " + key + " returns null");
			return null;
		} catch (Exception ce) {			
			LOGGER.error(ce.getMessage(), ce);
			ce.printStackTrace();
			throw new RuntimeException(ce);
		}
	}

	public Object get(String fullyQualifiedName) {
		LOGGER.info("get("+fullyQualifiedName+")");
		
		try {
			if (!manager.cacheExists(fullyQualifiedName)) {
				//manager.addCache(fullyQualifiedName);
				LOGGER.info("cache not exists and return: null");
				return null;
			}
			Cache cache = manager.getCache(fullyQualifiedName);
			return cache.getAllWithLoader(cache.getKeys(), null);
		} catch (Exception ce) {
			LOGGER.error("EhCache could not be shutdown", ce);
			throw new RuntimeException(ce);
		}
				
	}

	public Collection<Object> getValues(String fullyQualifiedName) {
		LOGGER.info("getValues("+fullyQualifiedName+")");
		try {
			if (!manager.cacheExists(fullyQualifiedName)) {
				//manager.addCache(fullyQualifiedName);
				LOGGER.info("cache not exists and return: empty collection");
				return new ArrayList<Object>();
			}
			Cache cache = manager.getCache(fullyQualifiedName);
			List<Object> values = new ArrayList<Object>();
			List<?> keys = cache.getKeys();
			
			for (Iterator<?> iter = keys.iterator(); iter.hasNext(); ) {
				Element element = cache.get((Serializable)iter.next());
				if (element != null) {
				    values.add(element.getValue());
				} else {
					LOGGER.info("element is null");
				}
			}
			
			LOGGER.info("return:" + values);

			return values;
		} catch (Exception ce) {
			LOGGER.error("EhCache could not be shutdown", ce);
			ce.printStackTrace();
			throw new RuntimeException(ce);
		}
	}

	public void remove(String fullyQualifiedName, String key) {
		LOGGER.info("remove("+fullyQualifiedName+", " + key +")");
		try {
			Cache cache = manager.getCache(fullyQualifiedName);

			if (cache != null) {
				cache.remove(key);
			}
		} catch (Exception ce) {
			ce.printStackTrace();
			throw new RuntimeException(ce);
		}
	}

	public void remove(String fullyQualifiedName) {
		LOGGER.info("remove("+fullyQualifiedName +")");
		try {
			if (manager.cacheExists(fullyQualifiedName)) {
				manager.removeCache(fullyQualifiedName);
			}
		} catch (Exception ce) {
			ce.printStackTrace();
			throw new RuntimeException(ce);
		}
	}

}

package net.jforum.repository;

import java.util.Map;

import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.RegEmailDAO;
import net.jforum.entities.Group;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.apache.commons.lang3.StringUtils;

public class RegEmailRepository {

	private static Map<String, Group> cache;

	static {
		load();
	}

	public static void load() {
		try {
		    final RegEmailDAO regEmailDao = DataAccessDriver.getInstance().newRegEmailDAO();
            cache = regEmailDao.selectAll();
		} catch (Exception e) {
			throw new RuntimeException("Error loading reg emails: ", e);
		}
	}

	public static int size() {
		return (cache != null ? cache.size() : 0);
	}

	public static int canRegister (final String email) {
		if (StringUtils.isBlank(email))
			return -1;

		// if ther are no restrictions, allow registration and put user in default group
		if (cache.isEmpty())
			return SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP);

		// if there are restrictions, check each one if it allows registration
		// if so, return the group to put the user in
		for (Map.Entry<String, Group> entry : cache.entrySet()) {
			String domain = entry.getKey();
			if (email.endsWith(domain))
				return entry.getValue().getId();
		}

		// there are restrictions, but the user does not match any of them
		// put in default group IF that has been explicitly allowed
        if (SystemGlobals.getBoolValue(ConfigKeys.REGISTRATION_EMAIL_NOT_MATCHING_ENABLED))
			return SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP);

		// otherwise, disallow registration
		return -1;
	}
}


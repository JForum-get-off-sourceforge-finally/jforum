package net.jforum.dao.generic;

import java.sql.*;
import java.util.*;

import net.jforum.*;
import net.jforum.dao.*;
import net.jforum.exceptions.*;
import net.jforum.util.*;
import net.jforum.util.preferences.*;

public class GenericAnnouncementDAO implements AnnouncementDAO {

	public static final String ANNOUNCEMENT_KEY = "announcement";

	public void delete() {
		try {
			Preferences.setValue(ANNOUNCEMENT_KEY, "");
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	public String select() {
		try {
			return Preferences.getStringValue(ANNOUNCEMENT_KEY, "");
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}

	public void update(String newValue) {
		try {
			Preferences.setValue(ANNOUNCEMENT_KEY, newValue);
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
	}
}

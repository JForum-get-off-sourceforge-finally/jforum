package net.jforum.repository;

import net.jforum.dao.*;

/**
 * Not threadsafe
 */
public class AnnouncementRepository {

	private static String announce = null;

	public static String getAnnouncement() {
		if (announce == null) {
			announce = DataAccessDriver.getInstance().newAnnouncementDAO().select();
		}
		return announce;
	}

	public static void setAnnouncement(String newAnnounce) {
		announce = newAnnounce;
	}

}

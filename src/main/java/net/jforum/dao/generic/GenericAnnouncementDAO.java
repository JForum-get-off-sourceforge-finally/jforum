package net.jforum.dao.generic;

import java.sql.*;
import java.util.*;

import net.jforum.*;
import net.jforum.dao.*;
import net.jforum.exceptions.*;
import net.jforum.util.*;
import net.jforum.util.preferences.*;

/**
 * Data stored in a sequence of 255 character chunks so can have arbitrary
 * length announcement.
 */
public class GenericAnnouncementDAO implements AnnouncementDAO {

	private static final int FIELD_SIZE = 255;

	public void delete() {
		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("AnnouncementDAO.deleteAll"));

			p.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(p);
		}
	}

	public String select() {
		PreparedStatement p = null;
		ResultSet rs = null;
		StringBuilder result = new StringBuilder();
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("AnnouncementDAO.select"));

			rs = p.executeQuery();

			while (rs.next()) {
				result.append(rs.getString(1));
			}

			return result.toString();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(rs, p);
		}
	}

	public void update(String newValue) {
		delete();

		PreparedStatement p = null;
		try {
			p = JForumExecutionContext.getConnection().prepareStatement(
					SystemGlobals.getSql("AnnouncementDAO.insert"));

			List<String> chunks = splitIntoChunks(newValue);
			int length = chunks.size();
			for (int i = 0; i < length; i++) {
				p.setInt(1, i);
				p.setString(2, chunks.get(i));

				p.addBatch();
			}

			p.executeBatch();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		} finally {
			DbUtils.close(p);
		}
	}

	private List<String> splitIntoChunks(String original) {
		String remainder = original;
		List<String> result = new ArrayList<String>();

		do {
			String oneString = remainder;
			if (remainder.length() <= FIELD_SIZE) {
				oneString = remainder;
				remainder = "";
			} else {
				oneString = remainder.substring(0, FIELD_SIZE);
				remainder = remainder.substring(FIELD_SIZE);
			}

			result.add(oneString);

		} while (remainder.length() > 0);

		return result;
	}
}

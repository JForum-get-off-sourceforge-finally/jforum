package net.jforum.dao;

import net.jforum.entities.Group;

import java.util.SortedMap;

/**
 * Model interface for allowed registration emails/domains. <p/>
 *
 * This interface defines methods which are expected to be
 * implemented by a specific data access driver. The intention is to provide
 * all functionality needed to insert, delete and select some specific data.
 */

public interface RegEmailDAO {
    /**
     * Returns all the registration emails/domains and associated groups currently in the database.
     */
	SortedMap<String, Group> selectAll();

    /**
     * Adds the specified email/domain to the database
     */
    void addRegEmail (String email, Group group);

    /**
     * Removes the specified email/domain from the database
     */
    void deleteRegEmail (String email);
}

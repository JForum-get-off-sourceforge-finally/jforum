package net.jforum.dao;

/**
 * Interface for the announcement that appears on the main page header.
 */
public interface AnnouncementDAO {
	
	/**
	 * Clear announcement
	 */
	public void delete();
	
	/**
	 * Delete existing announcement and replace with this one
	 * @param newValue
	 */
	public void update(String newValue);
	
	/**
	 * Select existing banner contents
	 * @return
	 */
	public String select();

}

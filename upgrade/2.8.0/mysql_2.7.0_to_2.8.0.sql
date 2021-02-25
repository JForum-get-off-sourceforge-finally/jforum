--
-- Table structure for table 'jforum_registrations'
--
DROP TABLE IF EXISTS jforum_registrations;
CREATE TABLE jforum_registrations (
	email VARCHAR(100) NOT NULL,
	group_id INT NOT NULL
) ENGINE=InnoDB;

ALTER TABLE jforum_registrations ADD FOREIGN KEY (group_id) REFERENCES jforum_groups(group_id);


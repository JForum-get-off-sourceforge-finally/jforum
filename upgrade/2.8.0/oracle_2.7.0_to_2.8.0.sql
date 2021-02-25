--
-- Table structure for table 'jforum_registrations'
--
CREATE TABLE jforum_registrations (
	email VARCHAR2(100) NOT NULL,
	group_id NUMBER(10) NOT NULL
);

ALTER TABLE jforum_registrations ADD FOREIGN KEY (group_id) REFERENCES jforum_groups(group_id);


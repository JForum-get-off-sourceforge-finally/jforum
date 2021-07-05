--
-- Table structure for table 'jforum_registrations'
--
CREATE TABLE jforum_registrations (
	email VARCHAR2(100) NOT NULL,
	group_id NUMBER(10) NOT NULL
);

ALTER TABLE jforum_registrations ADD FOREIGN KEY (group_id) REFERENCES jforum_groups(group_id);

-- widen config field for long entries
ALTER TABLE jforum_banner MODIFY banner_name TYPE VARCHAR2(1024);


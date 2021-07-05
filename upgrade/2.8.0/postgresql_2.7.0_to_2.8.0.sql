--
-- Table structure for table 'jforum_registrations'
--
CREATE TABLE jforum_registrations (
  email VARCHAR(100) NOT NULL,
  group_id INT NOT NULL
);

ALTER TABLE jforum_registrations ADD FOREIGN KEY (group_id) REFERENCES jforum_groups(group_id);

ALTER TABLE jforum_banner ALTER COLUMN banner_name TYPE VARCHAR(1024);

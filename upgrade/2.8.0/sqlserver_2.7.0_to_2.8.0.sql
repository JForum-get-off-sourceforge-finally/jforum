--
-- Table structure for table 'jforum_registrations'
--
CREATE TABLE jforum_registrations (
  email nvarchar(100) NOT NULL,
  group_id INT NOT NULL
);

ALTER TABLE jforum_registrations ADD FOREIGN KEY (group_id) REFERENCES jforum_groups(group_id);


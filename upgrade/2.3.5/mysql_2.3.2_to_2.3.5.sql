ALTER TABLE jforum_users ADD COLUMN user_twitter VARCHAR(50) DEFAULT NULL;
--
-- Table structure for table 'jforum_announcement'
--
DROP TABLE IF EXISTS jforum_announcement;
CREATE TABLE jforum_announcement (
  sequence_number INT NOT NULL,
  text VARCHAR(1024) NOT NULL,
  PRIMARY KEY (sequence_number)
) ENGINE=InnoDB;
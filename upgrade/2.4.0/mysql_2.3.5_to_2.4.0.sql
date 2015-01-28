--
-- Table structure for table 'jforum_spam'
--
DROP TABLE IF EXISTS jforum_spam;
CREATE TABLE jforum_spam (
  pattern VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- more characters for more secure hash
ALTER TABLE jforum_users MODIFY user_password VARCHAR(128);


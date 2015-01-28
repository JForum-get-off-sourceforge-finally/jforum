--
-- Table structure for table 'jforum_spam'
--
CREATE TABLE jforum_spam (
  pattern VARCHAR(100) NOT NULL
);

-- more characters for more secure hash
ALTER TABLE jforum_users ALTER COLUMN user_password VARCHAR(128);


--
-- Table structure for table 'jforum_spam'
--
CREATE TABLE jforum_spam (
  pattern varchar(100) NOT NULL
);

-- more characters for more secure hash
ALTER TABLE jforum_users ALTER COLUMN user_password TYPE VARCHAR(128);


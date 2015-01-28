--
-- Table structure for table 'jforum_spam'
--
CREATE TABLE jforum_spam (
  pattern VARCHAR2(100) NOT NULL
);

-- more characters for more secure hash
ALTER TABLE jforum_users MODIFY (
	user_password VARCHAR(128)
);


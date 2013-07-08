ALTER TABLE jforum_users ADD COLUMN user_twitter VARCHAR(50) DEFAULT NULL;

ALTER TABLE jforum_vote_voters ALTER COLUMN vote_user_ip varchar(15);

ALTER TABLE jforum_words ALTER COLUMN word SET DEFAULT ('');

--
-- Table structure for table 'jforum_announcement'
--
CREATE TABLE jforum_announcement (
  sequence_number int PRIMARY KEY NOT NULL,
  text nvarchar(1024) NOT NULL
);
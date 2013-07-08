ALTER TABLE jforum_users ADD COLUMN user_twitter VARCHAR2(50) DEFAULT NULL;

ALTER TABLE jforum_moderation_log ALTER COLUMN post_id SET DEFAULT 0;
ALTER TABLE jforum_moderation_log ALTER COLUMN topic_id SET DEFAULT 0;
ALTER TABLE jforum_moderation_log ALTER COLUMN post_user_id SET DEFAULT 0;

ALTER TABLE jforum_mail_integration MODIFY pop_port NUMBER(5);

--
-- Table structure for table 'jforum_announcement'
--
CREATE TABLE jforum_announcement (
  sequence_number NUMBER(10) NOT NULL,
  text VARCHAR2(1024) NOT NULL,
  PRIMARY KEY(sequence_number)
);

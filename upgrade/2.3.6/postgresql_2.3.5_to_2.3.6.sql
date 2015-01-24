--
-- Table structure for table 'jforum_spam'
--
CREATE TABLE jforum_spam (
  pattern VARCHAR(100) NOT NULL
);

ALTER TABLE jforum_topics ALTER COLUMN topic_title TYPE VARCHAR(120);
ALTER TABLE jforum_posts_text ALTER COLUMN post_subject TYPE VARCHAR(130);
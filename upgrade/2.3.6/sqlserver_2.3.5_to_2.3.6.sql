--
-- Table structure for table 'jforum_spam'
--
CREATE TABLE jforum_spam (
  pattern nvarchar(100) NOT NULL
);

ALTER TABLE jforum_topics ALTER COLUMN topic_title nvarchar(120);
ALTER TABLE jforum_posts_text ALTER COLUMN post_subject nvarchar(130);
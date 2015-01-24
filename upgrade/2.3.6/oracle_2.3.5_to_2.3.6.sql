--
-- Table structure for table 'jforum_spam'
--
CREATE TABLE jforum_spam (
  pattern VARCHAR2(100) NOT NULL
);

ALTER TABLE jforum_topics MODIFY topic_title VARCHAR2(120);
ALTER TABLE jforum_posts_text MODIFY post_subject VARCHAR2(130);
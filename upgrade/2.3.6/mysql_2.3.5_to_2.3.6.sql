--
-- Table structure for table 'jforum_spam'
--
DROP TABLE IF EXISTS jforum_spam;
CREATE TABLE jforum_spam (
  pattern VARCHAR(100) NOT NULL
) ENGINE=InnoDB;
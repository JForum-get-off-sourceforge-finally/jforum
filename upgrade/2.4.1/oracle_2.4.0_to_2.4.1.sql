DROP SEQUENCE jforum_themes_seq;
DROP TABLE  jforum_themes;

ALTER TABLE jforum_users DROP (gender, themes_id);
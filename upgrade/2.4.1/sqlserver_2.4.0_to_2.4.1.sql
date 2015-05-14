IF EXISTS (SELECT * FROM dbo.sysobjects WHERE id = object_id(N'jforum_themes') AND OBJECTPROPERTY(id, N'IsUserTable') = 1)		
DROP TABLE jforum_themes;

ALTER TABLE jforum_users DROP COLUMN gender;
ALTER TABLE jforum_users DROP COLUMN themes_id;
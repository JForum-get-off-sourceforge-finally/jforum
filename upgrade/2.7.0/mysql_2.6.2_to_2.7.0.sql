-- add Skype ID
ALTER TABLE jforum_users ADD COLUMN user_skype VARCHAR(50) DEFAULT NULL;

-- remove obsolete columns
ALTER TABLE jforum_users DROP user_viewonline;
ALTER TABLE jforum_users DROP user_allow_viewonline;
ALTER TABLE jforum_users DROP user_aim;
ALTER TABLE jforum_users DROP user_yim;
ALTER TABLE jforum_users DROP user_msnm;

-- widen config field for long entries
ALTER TABLE jforum_config MODIFY config_value TYPE VARCHAR(1024);


-- add Skype ID
ALTER TABLE jforum_users ADD COLUMN user_skype VARCHAR(50) DEFAULT NULL;

-- remove obsolete columns
ALTER TABLE jforum_users DROP user_viewonline;
ALTER TABLE jforum_users DROP user_allow_viewonline;
ALTER TABLE jforum_users DROP user_aim;
ALTER TABLE jforum_users DROP user_yim;
ALTER TABLE jforum_users DROP user_msnm;

-- widen config field for long entries
ALTER TABLE jforum_config ALTER COLUMN config_value TYPE VARCHAR(1024);

-- new smilies
INSERT INTO jforum_smilies VALUES (33, ':beerchug:', '<img src=\"#CONTEXT#/images/smilies/cc6690697b91b8cd32696ed6f361cbe4.gif\" alt=\"smilie\" />', 'cc6690697b91b8cd32696ed6f361cbe4.gif');
INSERT INTO jforum_smilies VALUES (34, ':roflmao:', '<img src=\"#CONTEXT#/images/smilies/3c27b0c44b9f840665edd9a2d24b57f3.gif\" alt=\"smilie\" />', '3c27b0c44b9f840665edd9a2d24b57f3.gif');
INSERT INTO jforum_smilies VALUES (35, ':jumping:', '<img src=\"#CONTEXT#/images/smilies/a8bb6599aceabf44663433fc99ad1db0.gif\" alt=\"smilie\" />', 'a8bb6599aceabf44663433fc99ad1db0.gif');
INSERT INTO jforum_smilies VALUES (36, ':banghead:', '<img src=\"#CONTEXT#/images/smilies/973c8eb2b9dcd92cecf8187c64761ef6.gif\" alt=\"smilie\" />', '973c8eb2b9dcd92cecf8187c64761ef6.gif');
INSERT INTO jforum_smilies VALUES (37, ':censored:', '<img src=\"#CONTEXT#/images/smilies/33dccaa5ed264b734370116faf09d1c8.gif\" alt=\"smilie\" />', '33dccaa5ed264b734370116faf09d1c8.gif');
INSERT INTO jforum_smilies VALUES (38, ':pissed:', '<img src=\"#CONTEXT#/images/smilies/8c9fac5d3e7cba173210082669b0316e.gif\" alt=\"smilie\" />', '8c9fac5d3e7cba173210082669b0316e.gif');
INSERT INTO jforum_smilies VALUES (39, ':sleep:', '<img src=\"#CONTEXT#/images/smilies/28f230537468150d34a2fb360c0d923f.gif\" alt=\"smilie\" />', '28f230537468150d34a2fb360c0d923f.gif');
INSERT INTO jforum_smilies VALUES (40, ':confused:', '<img src=\"#CONTEXT#/images/smilies/4d5a6f04e1481e0c1c4ad7ef4957b4c8.gif\" alt=\"smilie\" />', '4d5a6f04e1481e0c1c4ad7ef4957b4c8.gif');

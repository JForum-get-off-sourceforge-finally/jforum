--
-- Groups
--
INSERT INTO jforum_groups (group_id, group_name, group_description ) VALUES (1,'General', 'General Users');
INSERT INTO jforum_groups (group_id, group_name, group_description ) VALUES (2,'Administration', 'Admin Users');

-- 
-- Users
--
INSERT INTO jforum_users (user_id, username, user_password ) VALUES (1,'Anonymous', 'nopass');
INSERT INTO jforum_users (user_id, username, user_password, user_posts ) VALUES (2,'Admin', '21232f297a57a5a743894a0e4a801fc3', 1);

--
-- User Groups
--
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (1, 1);
INSERT INTO jforum_user_groups (group_id, user_id) VALUES (2, 2);

--
-- Smilies
--
INSERT INTO jforum_smilies VALUES (1, ':)', '<img src=\"#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif\" alt=\"smilie\" />', '3b63d1616c5dfcf29f8a7a031aaa7cad.gif');
INSERT INTO jforum_smilies VALUES (2, ':-)', '<img src=\"#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif\" alt=\"smilie\" />', '3b63d1616c5dfcf29f8a7a031aaa7cad.gif');
INSERT INTO jforum_smilies VALUES (3, ':D', '<img src=\"#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif\" alt=\"smilie\" />', '283a16da79f3aa23fe1025c96295f04f.gif');
INSERT INTO jforum_smilies VALUES (4, ':-D', '<img src=\"#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif\" alt=\"smilie\" />', '283a16da79f3aa23fe1025c96295f04f.gif');
INSERT INTO jforum_smilies VALUES (5, ':(', '<img src=\"#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif\" alt=\"smilie\" />', '9d71f0541cff0a302a0309c5079e8dee.gif');
INSERT INTO jforum_smilies VALUES (6, ':mrgreen:', '<img src=\"#CONTEXT#/images/smilies/ed515dbff23a0ee3241dcc0a601c9ed6.gif\" alt=\"smilie\" />', 'ed515dbff23a0ee3241dcc0a601c9ed6.gif');
INSERT INTO jforum_smilies VALUES (7, ':-o', '<img src=\"#CONTEXT#/images/smilies/47941865eb7bbc2a777305b46cc059a2.gif\" alt=\"smilie\" />', '47941865eb7bbc2a777305b46cc059a2.gif');
INSERT INTO jforum_smilies VALUES (8, ':shock:', '<img src=\"#CONTEXT#/images/smilies/385970365b8ed7503b4294502a458efa.gif\" alt=\"smilie\" />', '385970365b8ed7503b4294502a458efa.gif');
INSERT INTO jforum_smilies VALUES (9, ':?:', '<img src=\"#CONTEXT#/images/smilies/0a4d7238daa496a758252d0a2b1a1384.gif\" alt=\"smilie\" />', '0a4d7238daa496a758252d0a2b1a1384.gif');
INSERT INTO jforum_smilies VALUES (10, '8)', '<img src=\"#CONTEXT#/images/smilies/b2eb59423fbf5fa39342041237025880.gif\" alt=\"smilie\" />', 'b2eb59423fbf5fa39342041237025880.gif');
INSERT INTO jforum_smilies VALUES (11, ':lol:', '<img src=\"#CONTEXT#/images/smilies/97ada74b88049a6d50a6ed40898a03d7.gif\" alt=\"smilie\" />', '97ada74b88049a6d50a6ed40898a03d7.gif');
INSERT INTO jforum_smilies VALUES (12, ':x', '<img src=\"#CONTEXT#/images/smilies/1069449046bcd664c21db15b1dfedaee.gif\" alt=\"smilie\" />', '1069449046bcd664c21db15b1dfedaee.gif');
INSERT INTO jforum_smilies VALUES (13, ':P', '<img src=\"#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif\" alt=\"smilie\" />', '69934afc394145350659cd7add244ca9.gif');
INSERT INTO jforum_smilies VALUES (14, ':-P', '<img src=\"#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif\" alt=\"smilie\" />', '69934afc394145350659cd7add244ca9.gif');
INSERT INTO jforum_smilies VALUES (15, ':oops:', '<img src=\"#CONTEXT#/images/smilies/499fd50bc713bfcdf2ab5a23c00c2d62.gif\" alt=\"smilie\" />', '499fd50bc713bfcdf2ab5a23c00c2d62.gif');
INSERT INTO jforum_smilies VALUES (16, ':cry:', '<img src=\"#CONTEXT#/images/smilies/c30b4198e0907b23b8246bdd52aa1c3c.gif\" alt=\"smilie\" />', 'c30b4198e0907b23b8246bdd52aa1c3c.gif');
INSERT INTO jforum_smilies VALUES (17, ':evil:', '<img src=\"#CONTEXT#/images/smilies/2e207fad049d4d292f60607f80f05768.gif\" alt=\"smilie\" />', '2e207fad049d4d292f60607f80f05768.gif');
INSERT INTO jforum_smilies VALUES (18, ':twisted:', '<img src=\"#CONTEXT#/images/smilies/908627bbe5e9f6a080977db8c365caff.gif\" alt=\"smilie\" />', '908627bbe5e9f6a080977db8c365caff.gif');
INSERT INTO jforum_smilies VALUES (19, ':roll:', '<img src=\"#CONTEXT#/images/smilies/2786c5c8e1a8be796fb2f726cca5a0fe.gif\" alt=\"smilie\" />', '2786c5c8e1a8be796fb2f726cca5a0fe.gif');
INSERT INTO jforum_smilies VALUES (20, ':wink:', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" alt=\"smilie\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies VALUES (21, ';)', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" alt=\"smilie\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies VALUES (22, ';-)', '<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" alt=\"smilie\" />', '8a80c6485cd926be453217d59a84a888.gif');
INSERT INTO jforum_smilies VALUES (23, ':!:', '<img src=\"#CONTEXT#/images/smilies/9293feeb0183c67ea1ea8c52f0dbaf8c.gif\" alt=\"smilie\" />', '9293feeb0183c67ea1ea8c52f0dbaf8c.gif');
INSERT INTO jforum_smilies VALUES (24, ':?', '<img src=\"#CONTEXT#/images/smilies/136dd33cba83140c7ce38db096d05aed.gif\" alt=\"smilie\" />', '136dd33cba83140c7ce38db096d05aed.gif');
INSERT INTO jforum_smilies VALUES (25, ':idea:', '<img src=\"#CONTEXT#/images/smilies/8f7fb9dd46fb8ef86f81154a4feaada9.gif\" alt=\"smilie\" />', '8f7fb9dd46fb8ef86f81154a4feaada9.gif');
INSERT INTO jforum_smilies VALUES (26, ':arrow:', '<img src=\"#CONTEXT#/images/smilies/d6741711aa045b812616853b5507fd2a.gif\" alt=\"smilie\" />', 'd6741711aa045b812616853b5507fd2a.gif');
INSERT INTO jforum_smilies VALUES (27, ':hunf:', '<img src=\"#CONTEXT#/images/smilies/0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif\" alt=\"smilie\" />', '0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif');
INSERT INTO jforum_smilies VALUES (28, ':-(', '<img src=\"#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif\" alt=\"smilie\" />', '9d71f0541cff0a302a0309c5079e8dee.gif');
INSERT INTO jforum_smilies VALUES (29, ':XD:', '<img src=\"#CONTEXT#/images/smilies/49869fe8223507d7223db3451e5321aa.gif\" alt=\"smilie\" />', '49869fe8223507d7223db3451e5321aa.gif');
INSERT INTO jforum_smilies VALUES (30, ':thumbup:', '<img src=\"#CONTEXT#/images/smilies/e8a506dc4ad763aca51bec4ca7dc8560.gif\" alt=\"smilie\" />', 'e8a506dc4ad763aca51bec4ca7dc8560.gif');
INSERT INTO jforum_smilies VALUES (31, ':thumbdown:', '<img src=\"#CONTEXT#/images/smilies/e78feac27fa924c4d0ad6cf5819f3554.gif\" alt=\"smilie\" />', 'e78feac27fa924c4d0ad6cf5819f3554.gif');
INSERT INTO jforum_smilies VALUES (32, ':|', '<img src=\"#CONTEXT#/images/smilies/1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif\" alt=\"smilie\" />', '1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif');
INSERT INTO jforum_smilies VALUES (33, ':beerchug:', '<img src=\"#CONTEXT#/images/smilies/cc6690697b91b8cd32696ed6f361cbe4.gif\" alt=\"smilie\" />', 'cc6690697b91b8cd32696ed6f361cbe4.gif');
INSERT INTO jforum_smilies VALUES (34, ':roflmao:', '<img src=\"#CONTEXT#/images/smilies/3c27b0c44b9f840665edd9a2d24b57f3.gif\" alt=\"smilie\" />', '3c27b0c44b9f840665edd9a2d24b57f3.gif');
INSERT INTO jforum_smilies VALUES (35, ':jumping:', '<img src=\"#CONTEXT#/images/smilies/a8bb6599aceabf44663433fc99ad1db0.gif\" alt=\"smilie\" />', 'a8bb6599aceabf44663433fc99ad1db0.gif');
INSERT INTO jforum_smilies VALUES (36, ':banghead:', '<img src=\"#CONTEXT#/images/smilies/973c8eb2b9dcd92cecf8187c64761ef6.gif\" alt=\"smilie\" />', '973c8eb2b9dcd92cecf8187c64761ef6.gif');
INSERT INTO jforum_smilies VALUES (37, ':censored:', '<img src=\"#CONTEXT#/images/smilies/33dccaa5ed264b734370116faf09d1c8.gif\" alt=\"smilie\" />', '33dccaa5ed264b734370116faf09d1c8.gif');
INSERT INTO jforum_smilies VALUES (38, ':pissed:', '<img src=\"#CONTEXT#/images/smilies/8c9fac5d3e7cba173210082669b0316e.gif\" alt=\"smilie\" />', '8c9fac5d3e7cba173210082669b0316e.gif');
INSERT INTO jforum_smilies VALUES (39, ':sleep:', '<img src=\"#CONTEXT#/images/smilies/28f230537468150d34a2fb360c0d923f.gif\" alt=\"smilie\" />', '28f230537468150d34a2fb360c0d923f.gif');
INSERT INTO jforum_smilies VALUES (40, ':confused:', '<img src=\"#CONTEXT#/images/smilies/4d5a6f04e1481e0c1c4ad7ef4957b4c8.gif\" alt=\"smilie\" />', '4d5a6f04e1481e0c1c4ad7ef4957b4c8.gif');
--
-- Demonstration Forum
--
INSERT INTO jforum_categories VALUES (1,'Category Test',1,0);
INSERT INTO jforum_forums VALUES (1,1,'Test Forum','This is a test forum',1,1,1,0);
INSERT INTO jforum_topics VALUES (1,1,'Welcome to JForum',2,CURRENT_TIMESTAMP,0,0,0,0,0,1,1,0,0);
INSERT INTO jforum_posts VALUES (1,1,1,2,CURRENT_TIMESTAMP,'127.0.0.1',1,0,1,1,null,0,1,0,0);
INSERT INTO jforum_posts_text VALUES (1,'[b][color=blue][size=18]Congratulations :!: [/size][/color][/b]\nYou have completed the installation, and JForum is up and running. \n\nTo start administering the board, login as [i]#ADMIN# / <the password you supplied in the installer>[/i] and access the [b][url=#FORUM_LINK#/admBase/login.page]Admin Control Panel[/url][/b] using the link that shows up in the bottom of the page. There you will be able to create Categories, Forums and much more  :D  \n\nFor more information and support, please refer to the following pages:\n\n:arrow: Community forum: https://community.jforum.net/\n:arrow: Documentation: https://sourceforge.net/p/jforum2/wiki2/Home\n\nThank you for choosing JForum.\n\nThe JForum Team\n\n','Welcome to JForum');

--
-- Roles
--
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_vote');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_karma_enabled');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_anonymous_post');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_create_poll');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_attachments_enabled');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_attachments_download');
INSERT INTO jforum_roles (group_id, name) VALUES (1, 'perm_moderation_log');

--
-- Admin
--
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_administration');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_post_remove');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_post_edit');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_topic_move');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_topic_lockUnlock');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_approve_messages');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_vote');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_create_poll');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_karma_enabled');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_attachments_enabled');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_attachments_download');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_moderation_log');
INSERT INTO jforum_roles (group_id, name) VALUES (2, 'perm_full_moderation_log');

--
-- View Forum
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_forum', 1);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_forum', 2);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

--
-- Anonymous posts
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_anonymous_post', 1);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_anonymous_post', 2);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');


--
-- View Category
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_category', 1);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_category', 2);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');


--
-- Sticky / Announcements
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_create_sticky_announcement_topics', 1);
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_create_sticky_announcement_topics', 2);

--
-- Create / Reply to topics
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_read_only_forums', 1);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');
	
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_read_only_forums', 2);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

-- 
-- Enable HTML
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_html_disabled', 1);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_html_disabled', 2);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');


--
-- Attachments
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_attachments_enabled', 1);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_attachments_enabled', 2);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

--
-- Reply only
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_reply_only', 1);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_reply_only', 2);
INSERT INTO jforum_role_values ( role_id, role_value) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');
	
--
-- Reply without moderation
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_reply_without_moderation',  1);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

INSERT INTO jforum_roles (name, group_id) VALUES ('perm_reply_without_moderation', 2);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

--
-- Moderation of forums
--
INSERT INTO jforum_roles (name, group_id) VALUES ('perm_moderation_forums', 2);
INSERT INTO jforum_role_values ( role_id, role_value ) VALUES ((SELECT MAX(role_id) FROM jforum_roles), '1');

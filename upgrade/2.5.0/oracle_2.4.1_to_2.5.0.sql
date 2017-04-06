
-- more characters for storing IP addresses (to accommodate IPv6) 
ALTER TABLE jforum_posts MODIFY poster_ip VARCHAR2(50);
ALTER TABLE jforum_privmsgs MODIFY privmsgs_ip VARCHAR2(50);
ALTER TABLE jforum_banlist MODIFY banlist_ip VARCHAR2(50);
ALTER TABLE jforum_sessions MODIFY session_ip VARCHAR2(50);
ALTER TABLE jforum_vote_voters MODIFY vote_user_ip VARCHAR2(50);


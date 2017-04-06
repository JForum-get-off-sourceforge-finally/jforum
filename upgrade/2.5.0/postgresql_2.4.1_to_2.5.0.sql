
-- more characters for storing IP addresses (to accommodate IPv6) 
ALTER TABLE jforum_posts ALTER COLUMN poster_ip TYPE VARCHAR(50);
ALTER TABLE jforum_privmsgs ALTER COLUMN privmsgs_ip TYPE VARCHAR(50);
ALTER TABLE jforum_banlist ALTER COLUMN banlist_ip TYPE VARCHAR(50);
ALTER TABLE jforum_sessions ALTER COLUMN session_ip TYPE VARCHAR(50);
ALTER TABLE jforum_vote_voters ALTER COLUMN vote_user_ip TYPE VARCHAR(50);

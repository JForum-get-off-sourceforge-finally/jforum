
-- more characters for storing IP addresses (to accommodate Ipv6) 
ALTER TABLE jforum_posts ALTER COLUMN poster_ip nvarchar(50);
ALTER TABLE jforum_privmsgs ALTER COLUMN privmsgs_ip nvarchar(50);
ALTER TABLE jforum_banlist ALTER COLUMN banlist_ip nvarchar(50);
ALTER TABLE jforum_sessions ALTER COLUMN session_ip nvarchar(50);
ALTER TABLE jforum_vote_voters ALTER COLUMN vote_user_ip nvarchar(50);


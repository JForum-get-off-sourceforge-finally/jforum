package net.jforum.util.rss;

import java.util.Iterator;
import java.util.List;

import net.jforum.entities.Forum;
import net.jforum.entities.Post;
import net.jforum.repository.ForumRepository;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.view.forum.common.PostCommon;
import net.jforum.view.forum.common.ViewCommon;

/**
 * RSS for user posts
 */
public class UserPostsRSS extends GenericRSS {
    
    protected String forumLink;
    protected RSS rss;
    
    public UserPostsRSS(String title, String description, int userId, List<Post> posts) {
        forumLink = ViewCommon.getForumLink();
        rss = new RSS(title, description, SystemGlobals.getValue(ConfigKeys.ENCODING), 
                this.forumLink + "posts/listByUser/" + userId);
        prepareRSS(posts);
    }

    private void prepareRSS(List<Post> posts) {
        for (Iterator iter = posts.iterator(); iter.hasNext();) {
            Post p = (Post) iter.next();
            
            Forum forum = ForumRepository.getForum(p.getForumId());

            p.setBbCodeEnabled(true);
            p.setHtmlEnabled(false);
            p.setHtmlEnabled(false);

            RSSItem item = new RSSItem();
            item.setAuthor(p.getPostUsername());
            item.setContentType(RSSAware.CONTENT_HTML);
			item.setDescription(PostCommon.preparePostForDisplay(p).getText());
            item.setPublishDate(RSSUtils.formatDate(p.getTime()));
            item.setTitle("["+forum.getName()+"] " + p.getSubject());
            item.setLink(this.forumLink + "posts/preList/" + p.getTopicId() + "/" + p.getId());

            rss.addItem(item);
        }

        super.setRSS(rss);
    }
}

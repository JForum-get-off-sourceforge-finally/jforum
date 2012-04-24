/*
 *	Class created on Jul 15, 2005
 */ 
package net.jforum.summary;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import net.jforum.TestCaseUtils;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.PostDAO;
import net.jforum.dao.TopicDAO;
import net.jforum.dao.UserDAO;
import net.jforum.entities.Post;
import net.jforum.entities.Topic;
import net.jforum.entities.User;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.quartz.SchedulerException;

import com.dumbster.smtp.SimpleSmtpServer;

/**
 * Test case for SummaryScheduler.
 * 
 * @see net.jforum.summary.SummaryScheduler
 * 
 * @author Franklin S. Dattein (<a href="mailto:franklin@hp.com">franklin@hp.com</a>)
 *
 */
public class SummaryTest extends TestCase 
{    

	private SimpleSmtpServer fakeSmtpServer;
	private Topic testTopic;
	private Post testPost;
	
	protected void setUp() throws Exception 
    {
        super.setUp();
        TestCaseUtils.loadEnvironment();
        TestCaseUtils.initDatabaseImplementation();
		final String quartzConfig = SystemGlobals.getValue(ConfigKeys.QUARTZ_CONFIG);
		SystemGlobals.loadAdditionalDefaults(quartzConfig);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_HOST, "localhost");
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PORT, "1825");
		fakeSmtpServer = SimpleSmtpServer.start(1825);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_SSL, String.valueOf(false));
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_AUTH, String.valueOf(false));		
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_USERNAME, "jforumtest@andowson.com");
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PASSWORD, "STQa*2eZ");

		SystemGlobals.setValue(ConfigKeys.SEARCH_INDEXING_ENABLED, String.valueOf(false));

		// we need a test post in the database for this testcase
		createTestPost();
    }

	private void createTestPost() {
		UserDAO userDAO = DataAccessDriver.getInstance().newUserDAO();
		User user = userDAO.findByName("Admin", true).get(0); // there's only one "Admin"
		
		testTopic = new Topic();
		testTopic.setForumId(1); // test forum id
		testTopic.setTitle("test topic");
		long twoDaysBefore = Calendar.getInstance().getTimeInMillis() - (2l * 1000 * 60 * 60 * 24);
		testTopic.setTime(new Date(twoDaysBefore));
		testTopic.setPostedBy(user); // admin's id
		TopicDAO topicDao = DataAccessDriver.getInstance().newTopicDAO();
		topicDao.addNew(testTopic);
		
		testPost = new Post();
        testPost.setEditTime(new Date(twoDaysBefore));
        testPost.setForumId(1); // test forumid
        testPost.setText("this is the post text");
        testPost.setSubject("this is the post subject");
        testPost.setTime(new Date(twoDaysBefore));
        testPost.setUserId(user.getId()); // admin's id
        testPost.setTopicId(testTopic.getId());
        PostDAO postDao = DataAccessDriver.getInstance().newPostDAO();
		postDao.addNew(testPost);

		testTopic.setFirstPostId(testPost.getId());
		testTopic.setLastPostId(testPost.getId());
		topicDao.update(testTopic);
	}
    
    protected void tearDown() throws Exception {
    	super.tearDown();
    	fakeSmtpServer.stop();
    	
    	deleteTestPost();
    }

	private void deleteTestPost() {
		// remove post
        PostDAO postDao = DataAccessDriver.getInstance().newPostDAO();
		postDao.delete(testPost);

    	// remove topic
		TopicDAO topicDao = DataAccessDriver.getInstance().newTopicDAO();
		topicDao.delete(testTopic, true);
	}
    
    
    /**
     * Tests only the scheduler and your frequency.
     * @throws Exception 
     *
     */
    public void testScheduler() throws Exception
    {                    
    	try {
            SummaryScheduler.startJob();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }        
    }
    
    public void testLoadRecipients() throws Exception
    {                         
    	SummaryModel model = new SummaryModel();
        Iterator<String> iter = model.listRecipients().iterator();
        while (iter.hasNext()) {            
            iter.next();            
        }
        assertTrue(model.listRecipients().size()>=0);       
    }
    
    public void testSendMails() throws Exception
    {
    	SummaryModel model = new SummaryModel();		
        //Do not uncomment this at least you want to send e-mails to all users.
        //List<String> recipients = model.listRecipients();
        List<String> recipients = new ArrayList<String>();
        //recipients.add("franklin@hp.com");
        recipients.add("jforumtest@andowson.com");
        
        model.sendPostsSummary(recipients);
    }
    
    public void testListPosts() throws Exception
    {       
    	SummaryModel model= new SummaryModel();
        // Gets a Date seven days before now
        long weekBefore = Calendar.getInstance().getTimeInMillis() - (7l * 1000 * 60 * 60 * 24);
        Date firstDate = new Date(weekBefore);
        Collection<Post> posts = model.listPosts(firstDate, new Date());
        Iterator<Post> iter = posts.iterator();
        while (iter.hasNext()) {
            iter.next();           
        }
        assertTrue(!posts.isEmpty());        
    }
}
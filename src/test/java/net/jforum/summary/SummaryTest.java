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
import net.jforum.entities.Post;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.quartz.SchedulerException;

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
    protected void setUp() throws Exception 
    {
        super.setUp();
        TestCaseUtils.loadEnvironment();
        TestCaseUtils.initDatabaseImplementation();
		final String quartzConfig = SystemGlobals.getValue(ConfigKeys.QUARTZ_CONFIG);
		SystemGlobals.loadAdditionalDefaults(quartzConfig);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_HOST, "smtp.gmail.com");
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PORT, "465");
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_SSL, String.valueOf(true));
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_AUTH, String.valueOf(true));		
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_USERNAME, "jforumtest@andowson.com");
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PASSWORD, "STQa*2eZ");
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
        long weekBefore = Calendar.getInstance().getTimeInMillis() - (7 * 1000 * 60 * 60 * 24);
        Date firstDate = new Date(weekBefore);
        Collection<Post> posts = model.listPosts(firstDate, new Date());
        Iterator<Post> iter = posts.iterator();
        while (iter.hasNext()) {
            iter.next();           
        }
        assertTrue(!posts.isEmpty());        
    }
}
package net.jforum.view.admin;

import java.util.SortedMap;

import org.apache.log4j.Logger;

import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.GroupDAO;
import net.jforum.dao.RegEmailDAO;
import net.jforum.entities.Group;
import net.jforum.repository.RegEmailRepository;
import net.jforum.util.I18n;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;
import net.jforum.util.preferences.TemplateKeys;

public class RegEmailAction extends AdminCommand {

    private static final Logger LOG = Logger.getLogger(RegEmailAction.class);

	private GroupDAO groupDao = DataAccessDriver.getInstance().newGroupDAO();
	private RegEmailDAO regEmailDao = DataAccessDriver.getInstance().newRegEmailDAO();

    @Override
    public void list() {
        final SortedMap<String, Group> regEmails = regEmailDao.selectAll();
        context.put("regEmails", regEmails);
        context.put("groups", groupDao.selectAll());
        context.put("defaultUserGroup", SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP));
        context.put("registrationEnabled", SystemGlobals.getBoolValue(ConfigKeys.REGISTRATION_ENABLED));
        context.put("registrationEmailNotMatchingEnabled", SystemGlobals.getBoolValue(ConfigKeys.REGISTRATION_EMAIL_NOT_MATCHING_ENABLED));
        setTemplateName(TemplateKeys.REG_EMAIL_LIST);
    }

    public void insert() {
        final String email = request.getParameter("email").trim();
        final int groupId = request.getIntParameter("group");

		Group group = groupDao.selectById(groupId);
		if (group.getId() == groupId) {
			LOG.info("Creating registration email/domain" + email + " in " + group);
			regEmailDao.addRegEmail(email, group);

			RegEmailRepository.load();
		} else {
			context.put("errorMessage", I18n.getMessage("RegEmail.noSuchGroup"));
			LOG.info("Group id="+groupId+" does not exist");
		}

        this.list();
    }

    public void delete() {
        final String email = request.getParameter("email");
        LOG.info("Deleting " + email);
        regEmailDao.deleteRegEmail(email);

		RegEmailRepository.load();
        this.list();
    }
}

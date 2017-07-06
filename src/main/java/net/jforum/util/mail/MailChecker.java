package net.jforum.util.mail;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;

/**
 * Does some quick plausibility checks on an email. 
 */

public class MailChecker
{
	private static final Logger log = Logger.getLogger(MailChecker.class);

	private static Pattern validEmail;

	// This list os obsolete, since there are now hundreds of TLDs
	private static List<String> gTLD = Arrays.asList(new String[] {
		"aero", "arpa", "asia", "biz", "cat", "com", "coop", "edu", "gov", "info", "int",
		"jobs", "mobi", "mil", "museum", "name", "net", "org", "pro", "tel", "travel" });

	static
	{
		try
		{
			// playing fast and loose: assuming that the TLD is at most 20 characters long
			validEmail = Pattern.compile("^\\S+@([-\\w]+\\.){1,4}[a-z]{2,20}$");
		}
		catch (PatternSyntaxException psex)
		{
			log.error("mail checking regexp could not be initialized");
		}
	}

	public static boolean checkEmail (String email)
	{
		if (email == null) {
			return false;
        }
		email = email.trim().toLowerCase();
		if (email.length() == 0) {
			return false;
        }

		// there can only be a single "@" character
		if (email.indexOf("@") != email.lastIndexOf("@")) {
			return false;
        }

		int dotIdx = email.lastIndexOf(".");
		if (dotIdx == -1) {
			return false;
        }

		// finally try the regex
		if (validEmail != null)
		{
			Matcher match = validEmail.matcher(email);
			return match.find();
		}

		return true;
	}
}

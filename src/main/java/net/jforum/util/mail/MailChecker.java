package net.jforum.util.mail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

/**
 * Does some quick plausibility checks on an email. 
 */

public class MailChecker
{
	private static final Logger log = Logger.getLogger(MailChecker.class);

	private static Pattern validEmail;

	static
	{
		try
		{
			// playing fast and loose: assuming that the TLD is at most 20 characters long,
			// which is true as of December 2020 according to https://en.wikipedia.org/wiki/List_of_Internet_top-level_domains
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
		if (email.isEmpty()) {
			return false;
        }

		// InternetAddress would not catch this: user@host is valid, but we don't want it
		int dotIdx = email.lastIndexOf(".");
		if (dotIdx == -1) {
			return false;
        }

		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			return false;
		}

		// finally try the regex - it won't catch much, given it's a valid InternetAddress,
		// but it does ensure that the TLD has at least 2 characters, and characters only
		if (validEmail != null)
		{
			Matcher match = validEmail.matcher(email);
			return match.find();
		}

		return true;
	}
}

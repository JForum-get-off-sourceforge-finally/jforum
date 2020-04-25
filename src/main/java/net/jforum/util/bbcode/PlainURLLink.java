
package net.jforum.util.bbcode;

/**
 * Transform a UBB tag like {@code [url]http://www.google.com/[/url]} into a link.
 * Replacement for the corresponding entry in bb_config.xml which was vulnerable to XSS attacks:<br>
 * <blockquote>
 *      {@code <regex>(?i)\[url\](.*?)\[/url\]</regex>}<br>
 *		{@code <replace><a class="snap_shots" href="$1" target="_new" rel="nofollow">$1</a></replace>}
 * </blockquote>
 */

public class PlainURLLink implements Substitution {

    @Override
    public String substitute (String url)
    {
		boolean suspicious = false;

		String changedUrl = url.trim();

		String lc = changedUrl.toLowerCase();
		suspicious |= (lc.indexOf("javascript:") != -1);
		suspicious |= (lc.indexOf("<script") != -1);

		if (suspicious)
        {
			return changedUrl;
        }

		return "<a class='snap_shots' href='" + changedUrl + "' target='_new' rel='nofollow'>" + changedUrl + "</a>";
    }
}


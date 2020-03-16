package net.jforum.util;

import junit.framework.TestCase;
import net.jforum.TestCaseUtils;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Rafael Steil
 */
public class SafeHtmlTest extends TestCase
{
	private static final String WELCOME_TAGS = "a, b, i, img";

	/** 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	protected void setUp() throws Exception
	{
		TestCaseUtils.loadEnvironment();

		SystemGlobals.setValue(ConfigKeys.HTML_TAGS_WELCOME, WELCOME_TAGS);
		SystemGlobals.setValue(ConfigKeys.HTML_LINKS_ALLOW_RELATIVE, "true");
		SystemGlobals.setValue(ConfigKeys.FORUM_LINK, "http://www.jorum.net/");
		SafeHtml.updateConfiguration();
	}

	@Test
	public void testMakeSafeSimple() throws Exception
	{
		String input = "text1 <b>textBold</b> text2 <i>textItalic</i> text3 <a href=\"relative\">linkrelative</a> text4 <a href=\"http://www.domain.com\">linkAbsolute</a>";

		String result = SafeHtml.makeSafe(input);

		assertEquals(input, result);
	}

	@Test
	public void testJavascriptInsideURLTagExpectItToBeRemoved()
	{
		String input = "<a class=\"snap_shots\" rel=\"nofollow\" target=\"_new\" onmouseover=\"javascript:alert('test2');\" href=\"before\">test</a>";
		String expected = "<a class=\"snap_shots\" rel=\"nofollow\" target=\"_new\" href=\"before\">test</a>";

		String result = SafeHtml.makeSafe(input);

		assertEquals(expected, result);
	}
	
	@Test
	public void testJavascriptInsideImageTagExpectItToBeRemoved()
	{
		String input = "<img border=\"0\" onmouseover=\"javascript:alert('buuuh!!!');\"\"\" src=\"javascript:alert('hi from an alert!');\">";
		String expected = "<img border=\"0\">";

		String result = SafeHtml.makeSafe(input);

		assertEquals(expected, result);
	}
	
	@Test
	public void testIframe() 
	{
		String input = "<iframe src='http://www.google.com' onload='javascript:parent.document.body.style.display=\'none\'; alert(\'where is the forum?\'); ' style='display:none;'></iframe>";
		String output = "";

		assertEquals(output, SafeHtml.makeSafe(input));
	}

	@Test
	public void testMakeSafe() throws Exception
	{
		StringBuilder input = new StringBuilder(1024);
		input.append("<a href=\"http://somelink\">Some Link</a>");
		input.append("bla <b>bla</b> <pre>code code</pre> test");
		input.append("<script>document.location = \"xxx\";</script>");
		input.append("<img src=\"http://imgPath\" onLoad=\"window.close();\">");
		input.append("<a href=\"javascript:alert(bleh)\">xxxx</a>");
		input.append("<img src=\"javascript:alert(bloh)\">");
		input.append("<img src=\"&#106ava&#115cript&#58aler&#116&#40&#39Oops&#39&#41&#59\">");
		input.append("\"> TTTTT <");
		input.append("<img src=\"http://some.image\" onLoad=\"javascript:alert(\"boo\")\">");
		input.append("<b>heeelooo, nurse</b>");
		input.append("<b style=\"some style\">1, 2, 3</b>");

		StringBuilder expected = new StringBuilder(1024);
		expected.append("<a href=\"http://somelink\">Some Link</a>");
		expected.append("bla <b>bla</b> code code test");
		expected.append("<img src=\"http://imgPath\">");
		expected.append("<a>xxxx</a>");
		expected.append("<img>");
		expected.append("<img>");
		expected.append("\"&gt; TTTTT &lt;");
		expected.append("<img src=\"http://some.image\">");
		expected.append("<b>heeelooo, nurse</b>");
		expected.append("<b>1, 2, 3</b>");

		assertEquals(expected.toString(), SafeHtml.makeSafe(input.toString()));
	}
}

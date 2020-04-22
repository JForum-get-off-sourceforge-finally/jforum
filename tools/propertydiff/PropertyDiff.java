 
import java.io.*;
import java.util.*;

/**
 * Produces a list of language properties that are missing, sorted by language, compared to BASE.
 * If passing in a parameter like "fr_FR", only missing properties for that language are shown.
 */

public class PropertyDiff {

	final static String DIR = "../../src/main/config/languages/";

	final static String BASE = "en_US";

	public static void main (String[] args) throws Exception {
		List<String> locales = new ArrayList<>();
		if (args.length > 0) {
			locales.add(args[0]);
		} else {
			locales.addAll(Arrays.asList("en_US", "pt_BR", "pt_PT", "it_IT", "nb_NO", "zh_TW",
					"hu_HU", "de_DE", "zh_CN", "ru_RU", "ja_JP", "fr_FR", "tr_TR", "nl_NL", "es_ES"));
		}
		Collections.sort(locales);

		Properties baseProps = new Properties();
		baseProps.load(new FileInputStream(DIR+BASE+".properties"));

		for (String loc : locales) {
			Properties testProps = new Properties();
			testProps.load(new FileInputStream(DIR+loc+".properties"));

			List<String> missing = new ArrayList<>();
			for (Enumeration names = baseProps.propertyNames(); names.hasMoreElements() ; ) {
				String name = (String) names.nextElement();
				if (! testProps.containsKey(name)) {
					missing.add(name+" = "+baseProps.getProperty(name));
				}
			}
			Collections.sort(missing);

			System.out.println();
			System.out.println(loc);
			for (String name : missing) {
				System.out.println(name);
			}
		}
	}
}

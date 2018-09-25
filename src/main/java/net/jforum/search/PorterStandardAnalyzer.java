package net.jforum.search;

import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import org.apache.log4j.Logger;

/**
 * Filters {@link StandardTokenizer} with {@link LowerCaseFilter},
 * {@link PorterStemFilter} and {@link StopFilter}, using a list of stop words.
 * Basically a StandardAnalyzer that also does Porter stemming.
 */

public final class PorterStandardAnalyzer extends StopwordAnalyzerBase {

	private static final Logger LOGGER = Logger.getLogger(PorterStandardAnalyzer.class);

	/** An set containing some common words that are usually not useful for searching.
	The lucene.analyzer.stopwords property determines the languages for which stop words are added. */
	private final static CharArraySet cas = new CharArraySet(CharArraySet.EMPTY_SET, true);

	/** Builds an analyzer with the stop words given in "cas" */
	public PorterStandardAnalyzer() {
		super(cas);
	}

	@Override
	protected TokenStreamComponents createComponents (final String fieldName) {
		// Filters StandardTokenizer with LowerCaseFilter, PorterStemFilter
		// and StopFilter, using a list of given stop words 
		// Basically a StandardAnalyzer that also does Porter stemming 
		final StandardTokenizer src = new StandardTokenizer();
		TokenStream tok = new LowerCaseFilter(src);
		tok = new StopFilter(tok, cas);
		tok = new PorterStemFilter(tok);
		return new TokenStreamComponents(src, tok);
	}

	static {
		String stopWordLanguages = SystemGlobals.getValue(ConfigKeys.LUCENE_STOPWORDs);
		for (String lang : stopWordLanguages.split(", ")) {
			LOGGER.debug("adding stop words for: "+lang);
			switch (lang) {
				case "ar":
					cas.addAll(org.apache.lucene.analysis.ar.ArabicAnalyzer.getDefaultStopSet());
					break;
				case "br":
					cas.addAll(org.apache.lucene.analysis.br.BrazilianAnalyzer.getDefaultStopSet());
					break;
				case "cz":
					cas.addAll(org.apache.lucene.analysis.cz.CzechAnalyzer.getDefaultStopSet());
					break;
				case "de":
					cas.addAll(org.apache.lucene.analysis.de.GermanAnalyzer.getDefaultStopSet());
					break;
				case "en":
					cas.addAll(org.apache.lucene.analysis.en.EnglishAnalyzer.getDefaultStopSet());
					break;
				case "fr":
					cas.addAll(org.apache.lucene.analysis.fr.FrenchAnalyzer.getDefaultStopSet());
					break;
				case "bg":
					cas.addAll(org.apache.lucene.analysis.bg.BulgarianAnalyzer.getDefaultStopSet());
					break;
				case "bn":
					cas.addAll(org.apache.lucene.analysis.bn.BengaliAnalyzer.getDefaultStopSet());
					break;
				case "ca":
					cas.addAll(org.apache.lucene.analysis.ca.CatalanAnalyzer.getDefaultStopSet());
					break;
				case "cjk":
					cas.addAll(org.apache.lucene.analysis.cjk.CJKAnalyzer.getDefaultStopSet());
					break;
				case "ckb":
					cas.addAll(org.apache.lucene.analysis.ckb.SoraniAnalyzer.getDefaultStopSet());
					break;
				case "da":
					cas.addAll(org.apache.lucene.analysis.da.DanishAnalyzer.getDefaultStopSet());
					break;
				case "el":
					cas.addAll(org.apache.lucene.analysis.el.GreekAnalyzer.getDefaultStopSet());
					break;
				case "es":
					cas.addAll(org.apache.lucene.analysis.es.SpanishAnalyzer.getDefaultStopSet());
					break;
				case "eu":
					cas.addAll(org.apache.lucene.analysis.eu.BasqueAnalyzer.getDefaultStopSet());
					break;
				case "fa":
					cas.addAll(org.apache.lucene.analysis.fa.PersianAnalyzer.getDefaultStopSet());
					break;
				case "fi":
					cas.addAll(org.apache.lucene.analysis.fi.FinnishAnalyzer.getDefaultStopSet());
					break;
				case "ga":
					cas.addAll(org.apache.lucene.analysis.ga.IrishAnalyzer.getDefaultStopSet());
					break;
				case "gl":
					cas.addAll(org.apache.lucene.analysis.gl.GalicianAnalyzer.getDefaultStopSet());
					break;
				case "hi":
					cas.addAll(org.apache.lucene.analysis.hi.HindiAnalyzer.getDefaultStopSet());
					break;
				case "hu":
					cas.addAll(org.apache.lucene.analysis.hu.HungarianAnalyzer.getDefaultStopSet());
					break;
				case "hy":
					cas.addAll(org.apache.lucene.analysis.hy.ArmenianAnalyzer.getDefaultStopSet());
					break;
				case "id":
					cas.addAll(org.apache.lucene.analysis.id.IndonesianAnalyzer.getDefaultStopSet());
					break;
				case "it":
					cas.addAll(org.apache.lucene.analysis.it.ItalianAnalyzer.getDefaultStopSet());
					break;
				case "lt":
					cas.addAll(org.apache.lucene.analysis.lt.LithuanianAnalyzer.getDefaultStopSet());
					break;
				case "lv":
					cas.addAll(org.apache.lucene.analysis.lv.LatvianAnalyzer.getDefaultStopSet());
					break;
				case "nl":
					cas.addAll(org.apache.lucene.analysis.nl.DutchAnalyzer.getDefaultStopSet());
					break;
				case "no":
					cas.addAll(org.apache.lucene.analysis.no.NorwegianAnalyzer.getDefaultStopSet());
					break;
				case "pt":
					cas.addAll(org.apache.lucene.analysis.pt.PortugueseAnalyzer.getDefaultStopSet());
					break;
				case "ro":
					cas.addAll(org.apache.lucene.analysis.ro.RomanianAnalyzer.getDefaultStopSet());
					break;
				case "ru":
					cas.addAll(org.apache.lucene.analysis.ru.RussianAnalyzer.getDefaultStopSet());
					break;
				case "sv":
					cas.addAll(org.apache.lucene.analysis.sv.SwedishAnalyzer.getDefaultStopSet());
					break;
				case "th":
					cas.addAll(org.apache.lucene.analysis.th.ThaiAnalyzer.getDefaultStopSet());
					break;
				case "tr":
					cas.addAll(org.apache.lucene.analysis.tr.TurkishAnalyzer.getDefaultStopSet());
					break;
				default:
					LOGGER.info("Language '"+lang+"' - don't know about stop words");
			}
		}
	}
}


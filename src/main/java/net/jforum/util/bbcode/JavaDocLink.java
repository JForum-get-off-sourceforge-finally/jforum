/*
 * Copyright (c) JForum Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms,
 * with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the
 * above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor
 * the names of its contributors may be used to endorse
 * or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util.bbcode;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Transform a UBB tag like [javadoc]javax.servlet.http.HttpServletRequest[/javadoc]
 * into a link to the corresponding javadoc page.
 * If no package name is given, then java.lang is assumed.
 *
 * Class names can be <i>versioned</i> if for some reason it makes sense to refer to the
 * javadocs of various API versions. This is done by appending a version ID to the class name:
 * [javadoc]String:5[/javadoc] refers to the javadocs of Java 5, while [javadoc]String:1.4[/javadoc]
 * refers to the javadocs of Java 1.4. Without the version ID, the latest available version is used.
 */

public class JavaDocLink implements Substitution {

	// indicates that there are several URLs for different versions of an API
	private static final String VERSIONED =		"versioned";
	private static final String OTHER =			"all";
	private static final String JSE_KEY =		"JSE";
	private static final String JEE_KEY =		"JEE";
	private static final String JASPER_KEY =	"JASPER";
	private static final String TOMCAT_KEY =	"TOMCAT";

	private static Map<String, String> versionedUrls;

	static {
		versionedUrls = new ConcurrentHashMap<String, String>();

		// JSE; "1.5" and "5" are synonyms
        versionedUrls.put(JSE_KEY+":1.5", "https://docs.oracle.com/javase/1.5.0/docs/api/");
        versionedUrls.put(JSE_KEY+":5", "https://docs.oracle.com/javase/1.5.0/docs/api/");
        versionedUrls.put(JSE_KEY+":6", "https://docs.oracle.com/javase/6/docs/api/");
        versionedUrls.put(JSE_KEY+":7", "https://docs.oracle.com/javase/7/docs/api/");
        versionedUrls.put(JSE_KEY+":8", "https://docs.oracle.com/javase/8/docs/api/");
        versionedUrls.put(JSE_KEY+":9", "https://docs.oracle.com/javase/9/docs/api/");
        versionedUrls.put(JSE_KEY+":"+OTHER, "https://docs.oracle.com/javase/9/docs/api/");

		// JEE
		versionedUrls.put(JEE_KEY+":1.2", "https://docs.oracle.com/javaee/1.2.1/api/");
        versionedUrls.put(JEE_KEY+":1.3", "https://docs.oracle.com/javaee/1.3/api/");
        versionedUrls.put(JEE_KEY+":1.4", "https://docs.oracle.com/javaee/1.4/api/");
        versionedUrls.put(JEE_KEY+":5", "https://docs.oracle.com/javaee/5/api/");
        versionedUrls.put(JEE_KEY+":6", "https://docs.oracle.com/javaee/6/api/");
        versionedUrls.put(JEE_KEY+":7", "https://docs.oracle.com/javaee/7/api/");
        versionedUrls.put(JEE_KEY+":8", "https://javaee.github.io/javaee-spec/javadocs/");
        versionedUrls.put(JEE_KEY+":"+OTHER, "https://javaee.github.io/javaee-spec/javadocs/");

		// Tomcat
		versionedUrls.put(JASPER_KEY+":5.5", "https://tomcat.apache.org/tomcat-5.5-doc/jasper/docs/api/");
        versionedUrls.put(JASPER_KEY+":6", "https://tomcat.apache.org/tomcat-6.0-doc/api/");
        versionedUrls.put(JASPER_KEY+":7", "https://tomcat.apache.org/tomcat-7.0-doc/api/");
        versionedUrls.put(JASPER_KEY+":8", "https://tomcat.apache.org/tomcat-8.0-doc/api/");
        versionedUrls.put(JASPER_KEY+":9", "https://tomcat.apache.org/tomcat-9.0-doc/api/");
        versionedUrls.put(JASPER_KEY+":"+OTHER, "https://tomcat.apache.org/tomcat-9.0-doc/api/");

		versionedUrls.put(TOMCAT_KEY+":5.5", "https://tomcat.apache.org/tomcat-5.5-doc/catalina/docs/api/");
        versionedUrls.put(TOMCAT_KEY+":6", "https://tomcat.apache.org/tomcat-6.0-doc/api/");
        versionedUrls.put(TOMCAT_KEY+":7", "https://tomcat.apache.org/tomcat-7.0-doc/api/");
        versionedUrls.put(TOMCAT_KEY+":8", "https://tomcat.apache.org/tomcat-8.0-doc/api/");
        versionedUrls.put(TOMCAT_KEY+":9", "https://tomcat.apache.org/tomcat-9.0-doc/api/");
        versionedUrls.put(TOMCAT_KEY+":"+OTHER, "https://tomcat.apache.org/tomcat-9.0-doc/api/");
	}

    // Sun / Oracle
    private static final String J2SE_URL = VERSIONED+":"+JSE_KEY;
    private static final String J2EE_URL = VERSIONED+":"+JEE_KEY;
	// JavaFX is now at https://openjfx.io/javadoc/13/, but the URL structure has changed in a non-trivial way
	private static final String JAVAFX_URL = "https://docs.oracle.com/javafx/2/api/";
    private static final String JOGL_URL = "https://www.jogamp.org/deployment/v2.3.2/javadoc/jogl/javadoc/";
    private static final String JAVA3D_URL = "https://www.jogamp.org/deployment/java3d/1.6.0-final/javadoc/";
    private static final String JMF_URL = "https://docs.oracle.com/cd/E17802_01/j2se/javase/technologies/desktop/media/jmf/2.1.1/apidocs/";
    private static final String JAI_URL = "https://docs.oracle.com/cd/E17802_01/products/products/java-media/jai/forDevelopers/jai-apidocs/";
    private static final String JERSEY2_URL = "https://eclipse-ee4j.github.io/jersey.github.io/apidocs/latest/jersey/index.html";
    private static final String COM_SUN_MAIL_URL = "https://javaee.github.io/javamail/docs/api/";

    // Apache
    private static final String TOMCAT_URL = VERSIONED+":"+TOMCAT_KEY;
    private static final String JASPER_URL = VERSIONED+":"+JASPER_KEY;
    private static final String LOG4J_URL = "https://logging.apache.org/log4j/docs/api/";
    private static final String LOG4J2_URL = "https://logging.apache.org/log4j/2.x/log4j-api/apidocs/";
	private static final String LUCENE_URL = "https://lucene.apache.org/core/8_6_0/core/";
    private static final String POI_URL = "https://poi.apache.org/apidocs/";
    private static final String AXIS2_URL = "https://axis.apache.org/axis2/java/core/api/";
    private static final String XML_CRYPTO_URL = "https://santuario.apache.org/Java/api/";
    private static final String STRUTS2_URL = "https://struts.apache.org/maven/struts2-core/apidocs/";
    private static final String WICKET_URL = "https://ci.apache.org/projects/wicket/apidocs/6.x/";
    private static final String XMLBEANS_URL = "https://xmlbeans.apache.org/docs/3.0.0/reference/";
    private static final String TAPESTRY_URL = "https://tapestry.apache.org/current/apidocs/";
    private static final String WSS4J_URL = "https://ws.apache.org/wss4j/apidocs/";
    private static final String SHIRO_URL = "https://shiro.apache.org/static/current/apidocs/";

    // Apache Commons
    private static final String ACP = "https://commons.apache.org/proper/commons";
    private static final String COLLECTIONS_URL = ACP + "-collections/javadocs/api-release/";
    private static final String CLI_URL = ACP + "-cli/javadocs/api-release/";
    private static final String VALIDATOR_URL = ACP + "-validator/apidocs/";
    private static final String MATH_URL = ACP + "-math/javadocs/api-3.6.1/";
    private static final String JEXL_URL = ACP + "-jexl/apidocs/";
    private static final String JXPATH_URL = ACP + "-jxpath/apidocs/";
	private static final String IO_URL = ACP + "-io/javadocs/api-release/";
    private static final String FILEUPLOAD_URL = ACP + "-fileupload/apidocs/";
    private static final String DIGESTER_URL = ACP + "-digester/apidocs/";
	private static final String DBCP_URL = ACP + "-dbcp/apidocs/";
    private static final String CONFIGURATION_URL = ACP + "-configuration/apidocs/";
    private static final String CODEC_URL = ACP + "-codec/apidocs/";
    private static final String BEANUTILS_URL = ACP + "-beanutils/javadocs/v1.9.4/apidocs/";
    private static final String HTTPCLIENT_URL = "https://hc.apache.org/httpclient-3.x/apidocs/";
    private static final String HC_CLIENT_URL = "https://hc.apache.org/httpcomponents-client-ga/httpclient/apidocs/";
    private static final String HC_CORE_URL = "https://hc.apache.org/httpcomponents-core-5.0.x/httpcore5/apidocs/";
    private static final String HC_CORE_HTTP2_URL = "https://hc.apache.org/httpcomponents-core-5.0.x/httpcore5-h2/apidocs/";
    private static final String NET_URL = ACP + "-net/javadocs/api-3.7/";
    private static final String LANG_URL = ACP + "-lang/javadocs/api-release/";
    private static final String LOGGING_URL = ACP + "-logging/javadocs/api-release/";
    private static final String COMPRESS_URL = ACP + "-compress/javadocs/api-release/";
    private static final String POOL_URL = ACP + "-pool/apidocs/";

    // Other 3rd party
    private static final String JUNIT_URL = "https://junit.org/junit5/docs/current/api/";
    private static final String JUNIT_OLD_URL = "http://junit.sourceforge.net/junit3.8.1/javadoc/";
    private static final String ITEXT2_URL = "https://librepdf.github.io/OpenPDF/docs-1-3-17/";
    private static final String ITEXT_URL = "https://api.itextpdf.com/";
	private static final String PDFBOX_URL = "https://pdfbox.apache.org/docs/2.0.13/javadocs/";
    private static final String JFREECHART_URL = "http://www.jfree.org/jfreechart/api/gjdoc/";
    private static final String IMAGEJ_URL = "https://rsb.info.nih.gov/ij/developer/api/";
    private static final String XOM_URL = "http://www.xom.nu/apidocs/";
    private static final String JCIFS_URL = "https://jcifs.samba.org/src/docs/api/";
    private static final String ANDROID_URL = "https://developer.android.com/reference/";
    private static final String MPXJ_URL = "https://www.mpxj.org/apidocs/";
    private static final String HTMLUNIT_URL = "http://htmlunit.sourceforge.net/apidocs/";
    private static final String DOM4J_URL = "http://dom4j.sourceforge.net/dom4j-1.6.1/apidocs/";
    private static final String JDOM2_URL = "http://www.jdom.org/docs/apidocs/";
    private static final String SPRING_URL = "https://docs.spring.io/spring/docs/current/javadoc-api/";
    private static final String HIBERNATE_URL = "https://docs.jboss.org/hibernate/stable/entitymanager/api/";
    private static final String HIBERNATE_SEARCH_URL = "https://docs.jboss.org/hibernate/stable/search/api/";
    private static final String HIBERNATE_VALIDATOR_URL = "https://docs.jboss.org/hibernate/stable/validator/api/";
    private static final String QUARTZ_URL = "https://www.quartz-scheduler.org/api/2.3.0/";
    private static final String OSGI_URL_CORE = "https://www.osgi.org/javadoc/r6/core/";
    private static final String OSGI_URL_ENTERPRISE = "https://www.osgi.org/javadoc/r6/enterprise/";
	private static final String GOOGLE_GUAVA_URL = "https://guava.dev/releases/29.0-jre/api/docs/";
	private static final String JAXEN_URL = "http://www.cafeconleche.org/jaxen/apidocs/";
	private static final String FREEMARKER_URL = "https://freemarker.org/docs/api/";
	private static final String BOUNCYCASTLE_URL = "https://bouncycastle.org/docs/docs1.5on/";

    private static final String[][] URL_MAP = new String[][] {
        {"javax.activation", J2EE_URL},
        {"javax.annotation.security", J2EE_URL}, // 6
        {"javax.annotation.sql", J2EE_URL}, // 6
        {"javax.batch", J2EE_URL}, // 7
        {"javax.context", J2EE_URL}, // 6
        {"javax.decorator", J2EE_URL}, // 6
        {"javax.ejb", J2EE_URL},
        {"javax.el", J2EE_URL},
        {"javax.enterprise", J2EE_URL},
        {"javax.event", J2EE_URL}, // 6
        {"javax.faces", J2EE_URL},
        {"javax.inject", J2EE_URL}, // 6
        {"javax.jms", J2EE_URL},
        {"javax.json", J2EE_URL}, // 7
        {"javax.mail", J2EE_URL},
        {"com.sun.mail", COM_SUN_MAIL_URL},
        {"javax.management.j2ee", J2EE_URL}, // 7
        {"javax.persistence", J2EE_URL},
        {"javax.resource", J2EE_URL},
        {"javax.security.auth.message", J2EE_URL}, // 6
        {"javax.security.jacc", J2EE_URL},
        {"javax.servlet", J2EE_URL},
        {"javax.transaction", J2EE_URL},
        {"javax.validation", J2EE_URL}, // 6
        {"javax.webbeans", J2EE_URL}, // 6
        {"javax.websocket", J2EE_URL}, // 7
        {"javax.ws.rs", J2EE_URL}, // 6
        {"javax.xml.registry", J2EE_URL},
        {"javax.xml.rpc", J2EE_URL},

        {"java.applet", J2SE_URL},
        {"java.awt", J2SE_URL},
        {"java.beans", J2SE_URL},
        {"java.io", J2SE_URL},
        {"java.lang", J2SE_URL},
        {"java.math", J2SE_URL},
        {"java.net", J2SE_URL},
        {"java.nio", J2SE_URL},
        {"java.rmi", J2SE_URL},
        {"java.security", J2SE_URL},
        {"java.sql", J2SE_URL},
        {"java.text", J2SE_URL},
        {"java.time", J2SE_URL}, // 8
        {"java.util", J2SE_URL},
        {"javax.accessibility", J2SE_URL},
        {"javax.activity", J2SE_URL}, // 1.5
        {"javax.annotation", J2SE_URL}, // 6
        {"javax.crypto", J2SE_URL},
        {"javax.imageio", J2SE_URL},
        {"javax.jnlp", J2SE_URL},
        {"javax.jws", J2SE_URL},
        {"javax.lang", J2SE_URL}, // 6
        {"javax.management", J2SE_URL}, // 7
        {"javax.naming", J2SE_URL},
        {"javax.net", J2SE_URL},
        {"javax.print", J2SE_URL},
        {"javax.rmi", J2SE_URL},
        {"javax.script", J2SE_URL}, // 6
        {"javax.security", J2SE_URL},
        {"javax.sound", J2SE_URL},
        {"javax.sql", J2SE_URL},
        {"javax.swing", J2SE_URL},
        {"javax.tools", J2SE_URL}, // 6
        {"javax.xml", J2SE_URL}, // after all the other javax.xml subpackages in JEE
        {"org.ietf.jgss", J2SE_URL},
        {"org.omg", J2SE_URL},
        {"org.w3c.dom", J2SE_URL}, // after all the other W3C DOM subpackages in Common DOM
        {"org.xml.sax", J2SE_URL},

		{"javafx", JAVAFX_URL},
        {"javax.media.jai", JAI_URL},
        {"com.sun.j3d", JAVA3D_URL},
        {"javax.media.j3d", JAVA3D_URL},
        {"javax.vecmath", JAVA3D_URL},
        {"com.jogamp", JOGL_URL},
        {"javax.media.nativewindow", JOGL_URL},
        {"javax.media.opengl", JOGL_URL},
        {"javax.media", JMF_URL}, // after all the other javax.media subpackages in JAI, Java3D and JOGL
        {"org.glassfish.jersey", JERSEY2_URL},
        {"com.sun.research.ws.wadl", JERSEY2_URL},

        {"org.apache.lucene", LUCENE_URL},
        {"org.apache.poi", POI_URL},
        {"org.apache.log4j", LOG4J_URL},
        {"org.apache.logging.log4j", LOG4J2_URL},
        {"org.apache.axis2", AXIS2_URL},
        {"org.apache.struts2", STRUTS2_URL},
        {"com.opensymphony.xwork2", STRUTS2_URL},
        {"org.apache.wicket", WICKET_URL},
        {"org.apache.xmlbeans", XMLBEANS_URL},
        {"org.apache.shiro", SHIRO_URL},
        {"org.apache.tapestry5", TAPESTRY_URL},
        {"org.apache.ws.axis.security", WSS4J_URL},
        {"org.apache.ws.security", WSS4J_URL},
        {"org.apache.xml.security", XML_CRYPTO_URL},

        {"org.apache.commons.collections", COLLECTIONS_URL},
        {"org.apache.commons.cli", CLI_URL},
        {"org.apache.commons.validator", VALIDATOR_URL},
        {"org.apache.commons.math", MATH_URL},
        {"org.apache.commons.jexl", JEXL_URL},
        {"org.apache.commons.jxpath", JXPATH_URL},
        {"org.apache.commons.io", IO_URL},
        {"org.apache.commons.fileupload", FILEUPLOAD_URL},
        {"org.apache.commons.digester", DIGESTER_URL},
        {"org.apache.commons.dbcp", DBCP_URL},
        {"org.apache.commons.configuration", CONFIGURATION_URL},
        {"org.apache.commons.codec", CODEC_URL},
        {"org.apache.commons.beanutils", BEANUTILS_URL},
        {"org.apache.commons.httpclient", HTTPCLIENT_URL},
        {"org.apache.commons.net", NET_URL},
        {"org.apache.commons.lang", LANG_URL},
        {"org.apache.commons.logging", LOGGING_URL},
        {"org.apache.commons.compress", COMPRESS_URL},
        {"org.apache.commons.pool2", POOL_URL},
        {"org.apache.http", HC_CLIENT_URL},
        {"org.apache.hc.core5.http2", HC_CORE_HTTP2_URL},
        {"org.apache.hc.core5", HC_CORE_URL}, // after HC_CORE_HTTP2_URL

        {"org.apache.catalina", TOMCAT_URL},
        {"org.apache.coyote", TOMCAT_URL},
        {"org.apache.el", TOMCAT_URL},
        {"org.apache.jasper", JASPER_URL},
        {"org.apache.jk", TOMCAT_URL},
        {"org.apache.juli", TOMCAT_URL},
        {"org.apache.naming", TOMCAT_URL},
        {"org.apache.tomcat", TOMCAT_URL},

        {"ij", IMAGEJ_URL},
        {"junit", JUNIT_OLD_URL},
        {"org.junit", JUNIT_URL},
        {"org.hamcrest", JUNIT_URL},
        {"com.lowagie", ITEXT2_URL},
        {"com.itextpdf", ITEXT_URL},
        {"org.apache.pdfbox", PDFBOX_URL},
        {"org.jfree.chart", JFREECHART_URL},
        {"org.jfree.data", JFREECHART_URL},
        {"nu.xom", XOM_URL},
        {"jcifs", JCIFS_URL},
        {"android", ANDROID_URL},
        {"androidx", ANDROID_URL},
        {"dalvik", ANDROID_URL},
        {"com.android", ANDROID_URL},
        {"com.google.android", ANDROID_URL},
        {"org.xmlpull", ANDROID_URL},
        {"org.json", ANDROID_URL},
        {"com.gargoylesoftware.htmlunit", HTMLUNIT_URL},
        {"org.jdom2", JDOM2_URL},
        {"org.dom4j", DOM4J_URL},
        {"net.sf.mpxj", MPXJ_URL},
        {"org.springframework", SPRING_URL},
        {"org.hibernate.search", HIBERNATE_SEARCH_URL},
        {"org.hibernate.validator", HIBERNATE_VALIDATOR_URL},
        {"org.hibernate", HIBERNATE_URL}, // after the other org.hibernate subpackages
		{"org.quartz", QUARTZ_URL},
		{"org.osgi.framework", OSGI_URL_CORE},
		{"org.osgi.resource", OSGI_URL_CORE},
		{"org.osgi.service.condpermadmin", OSGI_URL_CORE},
		{"org.osgi.service.packageadmin", OSGI_URL_CORE},
		{"org.osgi.service.permissionadmin", OSGI_URL_CORE},
		{"org.osgi.service.startlevel", OSGI_URL_CORE},
		{"org.osgi.service.url", OSGI_URL_CORE},
		{"org.osgi.util.tracker", OSGI_URL_CORE},
		{"org.osgi", OSGI_URL_ENTERPRISE}, // after the other org.osgi packages that are part of the Core
		{"com.google.common", GOOGLE_GUAVA_URL },
		{"org.jaxen", JAXEN_URL },
		{"freemarker", FREEMARKER_URL },
		{"org.bouncycastle", BOUNCYCASTLE_URL }
    };

	private String lookup (String packageName, String apiVersion) {
        for (int i=0; i<URL_MAP.length; i++) {
            if (packageName.startsWith(URL_MAP[i][0])) {
				String url = URL_MAP[i][1];
				if (url.startsWith(VERSIONED)) {
					String versionKey = url.substring(url.indexOf(':')+1);
					String finalUrl = versionedUrls.get(versionKey+":"+apiVersion);
					if (finalUrl != null) {
						return finalUrl;
					}
					return versionedUrls.get(versionKey+":"+OTHER);
				}
				return url;
            }
        }

		return null;
	}

	// @Override
    @Override public String substitute (String clazzName)
    {
		// remove any leading or trailing whitespace
		clazzName = clazzName.trim();

		int colonIndex = clazzName.indexOf(':');
		String apiVersion = null;
		if (colonIndex != -1) {
			apiVersion = clazzName.substring(colonIndex+1);
			clazzName = clazzName.substring(0, colonIndex);
		}

        int lastDotIndex = clazzName.lastIndexOf('.');
		int hashIndex = clazzName.indexOf('#');
		// Handle page-internal hashes like java.lang.Object#equals(java.lang.Object)
		// Assume java.lang package if no package name is given
		if (hashIndex == -1) {
			if (lastDotIndex == -1) {
				clazzName = "java.lang." + clazzName;
				lastDotIndex = clazzName.lastIndexOf('.');
			}
		} else {
			lastDotIndex = clazzName.lastIndexOf('.', hashIndex);
			if (lastDotIndex == -1) {
				clazzName = "java.lang." + clazzName;
				hashIndex = clazzName.indexOf('#');
				lastDotIndex = clazzName.lastIndexOf('.', hashIndex);
			}
		}

        String packageName = clazzName.substring(0, lastDotIndex).toLowerCase();

		String url = lookup(packageName, apiVersion);
		if (url != null) {
				// http://java.sun.com/javase/6/docs/api/java/util/Map.Entry.html
			if (hashIndex != -1) {
				String part1 = replaceDots(clazzName.substring(0, hashIndex));
				String part2 = clazzName.substring(hashIndex);
				// parentheses can be left out if there are no parameters
				if (part2.indexOf('(') < 0) {
					clazzName += "()";
					part2 += "()";
				}
				if (url.equals(versionedUrls.get(JSE_KEY+":8"))
						||  url.equals(versionedUrls.get(JSE_KEY+":9"))) {
					// Java SE 8 introduces a new URL style
					part2 = part2.replaceAll("[)(]", "-");
				}

				return "<a class=\"snap_shots\" href=\"" + url + part1 + ".html" + part2 
					+ "\" target=\"_blank\" rel=\"nofollow\">" + clazzName + "</a>";
			} else {
				return "<a class=\"snap_shots\" href=\"" + url + replaceDots(clazzName)
					+ ".html\" target=\"_blank\" rel=\"nofollow\">" + clazzName + "</a>";
			}
		}

		// if nothing is matched, then the original classname is returned
        return clazzName;
    }

	/** 
	 * Dots are replaced by backslashes, except if the next character is uppercase
	 * or inside of parentheses. The method relies on package names being lowercase.
	 * That allows linking to inner classes like java.util.Map.Entry.
	 * and to method hashes like java.util.Map.Entry#equals(java.lang.Object)
	 */
	private String replaceDots (String clazzName) {
		StringBuilder sb = new StringBuilder(clazzName);
		boolean classNameHasStarted = false;
		for (int i=0; i<sb.length(); i++) {
			if (sb.charAt(i) == '.') {
				if (!classNameHasStarted)
					sb.setCharAt(i, '/');

				if (Character.isUpperCase(sb.charAt(i+1)))
					classNameHasStarted = true;
			}
		}
		return sb.toString();
	}
}


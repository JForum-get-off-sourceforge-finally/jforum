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

/**
 * Transform a UBB tag like [javadoc]javax.servlet.http.HttpServletRequest[/javadoc]
 * into a link to the corresponding javadoc page.
 * If no package name is given, then java.lang is assumed.
 */

public class JavaDocLink implements Substitution {

    // Sun / Oracle
    private static final String JAVASE_URL = "https://docs.oracle.com/javase/9/docs/api/";
    private static final String JAVAEE_URL = "https://javaee.github.io/javaee-spec/javadocs/";
    private static final String JAKARTAEE_URL = "https://jakarta.ee/specifications/platform/9/apidocs/";
	// JavaFX is now at https://openjfx.io/javadoc/13/, but the URL structure has changed in a non-trivial way
	private static final String JAVAFX_URL = "https://docs.oracle.com/javafx/2/api/";
    private static final String JOGL_URL = "https://www.jogamp.org/deployment/v2.3.2/javadoc/jogl/javadoc/";
    private static final String JAVA3D_URL = "https://www.jogamp.org/deployment/java3d/1.6.0-final/javadoc/";
    private static final String JMF_URL = "https://docs.oracle.com/cd/E17802_01/j2se/javase/technologies/desktop/media/jmf/2.1.1/apidocs/";
    private static final String JAI_URL = "https://docs.oracle.com/cd/E17802_01/products/products/java-media/jai/forDevelopers/jai-apidocs/";
    private static final String JERSEY2_URL = "https://eclipse-ee4j.github.io/jersey.github.io/apidocs/latest/jersey/index.html";
    private static final String COM_SUN_MAIL_URL = "https://javaee.github.io/javamail/docs/api/";

    // Apache
    private static final String TOMCAT_URL = "https://tomcat.apache.org/tomcat-9.0-doc/api/";
    private static final String LOG4J_URL = "https://logging.apache.org/log4j/docs/api/";
    private static final String LOG4J2_URL = "https://logging.apache.org/log4j/2.x/log4j-api/apidocs/";
	private static final String LUCENE_URL = "https://lucene.apache.org/core/8_7_0/core/";
    private static final String POI_URL = "https://poi.apache.org/apidocs/";
    private static final String AXIS2_URL = "https://axis.apache.org/axis2/java/core/api/";
    private static final String XML_CRYPTO_URL = "https://santuario.apache.org/Java/api/";
    private static final String STRUTS2_URL = "https://struts.apache.org/maven/struts2-core/apidocs/";
    private static final String WICKET_URL = "https://ci.apache.org/projects/wicket/apidocs/6.x/";
    private static final String XMLBEANS_URL = "https://xmlbeans.apache.org/docs/3.1.0/reference/";
    private static final String TAPESTRY_URL = "https://tapestry.apache.org/current/apidocs/";
    private static final String WSS4J_URL = "https://ws.apache.org/wss4j/apidocs/";
    private static final String SHIRO_URL = "https://shiro.apache.org/static/current/apidocs/";
    private static final String VELOCITY_URL = "https://velocity.apache.org/engine/2.2/apidocs/";
    private static final String VELOCITY_TOOLS_URL = "https://velocity.apache.org/tools/3.0/apidocs/";

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
    private static final String NET_URL = ACP + "-net/apidocs/";
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
    private static final String JFREECHART_URL = "https://www.jfree.org/jfreechart/api/gjdoc/";
    private static final String IMAGEJ_URL = "https://rsb.info.nih.gov/ij/developer/api/";
    private static final String XOM_URL = "http://www.xom.nu/apidocs/";
    private static final String JCIFS_URL = "https://jcifs.samba.org/src/docs/api/";
    private static final String ANDROID_URL = "https://developer.android.com/reference/";
    private static final String MPXJ_URL = "https://www.mpxj.org/apidocs/";
    private static final String HTMLUNIT_URL = "https://htmlunit.sourceforge.io/apidocs/";
    private static final String DOM4J_URL = "https://dom4j.github.io/javadoc/2.1.3/";
    private static final String JDOM2_URL = "http://www.jdom.org/docs/apidocs/";
    private static final String SPRING_URL = "https://docs.spring.io/spring/docs/current/javadoc-api/";
    private static final String HIBERNATE_URL = "https://docs.jboss.org/hibernate/stable/entitymanager/api/";
    private static final String HIBERNATE_SEARCH_URL = "https://docs.jboss.org/hibernate/stable/search/api/";
    private static final String HIBERNATE_VALIDATOR_URL = "https://docs.jboss.org/hibernate/stable/validator/api/";
    private static final String QUARTZ_URL = "https://www.quartz-scheduler.org/api/2.3.0/";
    private static final String OSGI_URL_CORE = "https://www.osgi.org/javadoc/r6/core/";
    private static final String OSGI_URL_ENTERPRISE = "https://www.osgi.org/javadoc/r6/enterprise/";
	private static final String GOOGLE_GUAVA_URL = "https://guava.dev/releases/30.0-jre/api/docs/";
	private static final String JAXEN_URL = "http://www.cafeconleche.org/jaxen/apidocs/";
	private static final String FREEMARKER_URL = "https://freemarker.org/docs/api/";
	private static final String BOUNCYCASTLE_URL = "https://bouncycastle.org/docs/docs1.5on/";
	private static final String EVENTBUS_URL = "https://greenrobot.org/files/eventbus/javadoc/3.0/";
    private static final String MARKENWERK_URL = "https://markenwerk.github.io/java-utils-mail-dkim/";

    private static final String[][] URL_MAP = new String[][] {
        {"javax.activation", JAVAEE_URL},
        {"javax.annotation.security", JAVAEE_URL}, // 6
        {"javax.annotation.sql", JAVAEE_URL}, // 6
        {"javax.batch", JAVAEE_URL}, // 7
        {"javax.context", JAVAEE_URL}, // 6
        {"javax.decorator", JAVAEE_URL}, // 6
        {"javax.ejb", JAVAEE_URL},
        {"javax.el", JAVAEE_URL},
        {"javax.enterprise", JAVAEE_URL},
        {"javax.event", JAVAEE_URL}, // 6
        {"javax.faces", JAVAEE_URL},
        {"javax.inject", JAVAEE_URL}, // 6
        {"javax.jms", JAVAEE_URL},
        {"javax.json", JAVAEE_URL}, // 7
        {"javax.mail", JAVAEE_URL},
        {"com.sun.mail", COM_SUN_MAIL_URL},
        {"javax.management.j2ee", JAVAEE_URL}, // 7
        {"javax.persistence", JAVAEE_URL},
        {"javax.resource", JAVAEE_URL},
        {"javax.security.auth.message", JAVAEE_URL}, // 6
        {"javax.security.jacc", JAVAEE_URL},
        {"javax.servlet", JAVAEE_URL},
        {"javax.transaction", JAVAEE_URL},
        {"javax.validation", JAVAEE_URL}, // 6
        {"javax.webbeans", JAVAEE_URL}, // 6
        {"javax.websocket", JAVAEE_URL}, // 7
        {"javax.ws.rs", JAVAEE_URL}, // 6
        {"javax.xml.registry", JAVAEE_URL},
        {"javax.xml.rpc", JAVAEE_URL},

        {"jakarta.activation", JAKARTAEE_URL},
        {"jakarta.annotation", JAKARTAEE_URL},
        {"jakarta.batch", JAKARTAEE_URL},
        {"jakarta.decorator", JAKARTAEE_URL},
        {"jakarta.ejb", JAKARTAEE_URL},
        {"jakarta.el", JAKARTAEE_URL},
        {"jakarta.enterprise", JAKARTAEE_URL},
        {"jakarta.faces", JAKARTAEE_URL},
        {"jakarta.inject", JAKARTAEE_URL},
        {"jakarta.interceptor", JAKARTAEE_URL},
        {"jakarta.jms", JAKARTAEE_URL},
        {"jakarta.json", JAKARTAEE_URL},
        {"jakarta.jws", JAKARTAEE_URL},
        {"jakarta.mail", JAKARTAEE_URL},
        {"jakarta.persistence", JAKARTAEE_URL},
        {"jakarta.resource", JAKARTAEE_URL},
        {"jakarta.security", JAKARTAEE_URL},
        {"jakarta.servlet", JAKARTAEE_URL},
        {"jakarta.transaction", JAKARTAEE_URL},
        {"jakarta.validation", JAKARTAEE_URL},
        {"jakarta.websocket", JAKARTAEE_URL},
        {"jakarta.ws", JAKARTAEE_URL},
        {"jakarta.xml", JAKARTAEE_URL},

        {"java.applet", JAVASE_URL},
        {"java.awt", JAVASE_URL},
        {"java.beans", JAVASE_URL},
        {"java.io", JAVASE_URL},
        {"java.lang", JAVASE_URL},
        {"java.math", JAVASE_URL},
        {"java.net", JAVASE_URL},
        {"java.nio", JAVASE_URL},
        {"java.rmi", JAVASE_URL},
        {"java.security", JAVASE_URL},
        {"java.sql", JAVASE_URL},
        {"java.text", JAVASE_URL},
        {"java.time", JAVASE_URL}, // 8
        {"java.util", JAVASE_URL},
        {"javax.accessibility", JAVASE_URL},
        {"javax.activity", JAVASE_URL}, // 1.5
        {"javax.annotation", JAVASE_URL}, // 6
        {"javax.crypto", JAVASE_URL},
        {"javax.imageio", JAVASE_URL},
        {"javax.jnlp", JAVASE_URL},
        {"javax.jws", JAVASE_URL},
        {"javax.lang", JAVASE_URL}, // 6
        {"javax.management", JAVASE_URL}, // 7
        {"javax.naming", JAVASE_URL},
        {"javax.net", JAVASE_URL},
        {"javax.print", JAVASE_URL},
        {"javax.rmi", JAVASE_URL},
        {"javax.script", JAVASE_URL}, // 6
        {"javax.security", JAVASE_URL},
        {"javax.sound", JAVASE_URL},
        {"javax.sql", JAVASE_URL},
        {"javax.swing", JAVASE_URL},
        {"javax.tools", JAVASE_URL}, // 6
        {"javax.xml", JAVASE_URL}, // after all the other javax.xml subpackages in JEE
        {"org.ietf.jgss", JAVASE_URL},
        {"org.omg", JAVASE_URL},
        {"org.w3c.dom", JAVASE_URL}, // after all the other W3C DOM subpackages in Common DOM
        {"org.xml.sax", JAVASE_URL},

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
        {"org.apache.velocity.tools", VELOCITY_TOOLS_URL},
        {"org.apache.velocity", VELOCITY_URL}, // after Velocity proper

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
        {"org.apache.jasper", TOMCAT_URL},
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
		{"org.bouncycastle", BOUNCYCASTLE_URL },
		{"org.greenrobot.eventbus", EVENTBUS_URL },
		{"net.markenwerk", MARKENWERK_URL }
    };

	private String lookup (String packageName) {
        for (int i=0; i<URL_MAP.length; i++) {
            if (packageName.startsWith(URL_MAP[i][0])) {
				return URL_MAP[i][1];
            }
        }

		return null;
	}

	// @Override
    @Override public String substitute (String clazzName)
    {
		// remove any leading or trailing whitespace
		clazzName = clazzName.trim();

		// different API versions used to be supported by suffixing them after a colon,
		// but no longer - just remove and ignore it
		int colonIndex = clazzName.indexOf(':');
		if (colonIndex != -1) {
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

		String url = lookup(packageName);
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
				// Java SE 8 introduces a new URL style
				part2 = part2.replaceAll("[)(]", "-");

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


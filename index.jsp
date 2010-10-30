<%@page import="net.jforum.util.preferences.*" %>
<%@page import="java.io.File" %>
<%
	String cfg = SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG);
	
	if (SystemGlobals.getBoolValue(ConfigKeys.INSTALLED) || (cfg != null && new File(cfg).exists())) {
	    response.setStatus(301);
	    response.setHeader("Location", "forums/list.page");
	} else {
		response.setStatus(301);
		response.setHeader("Location", "install.jsp");	
	}
%>
<%@page import="net.jforum.util.preferences.*" %>
<%@page import="java.io.File" %>
<%
	final File file = new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
	
	if (SystemGlobals.getBoolValue(ConfigKeys.INSTALLED) || file.exists()) {
	    response.setStatus(301);
	    response.setHeader("Location", "forums/list.page");
	} else {
		response.setStatus(301);
		response.setHeader("Location", "install.jsp");	
	}
%>
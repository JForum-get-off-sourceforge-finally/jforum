<%@page import="net.jforum.util.preferences.*" %>
<%
if (SystemGlobals.getBoolValue(ConfigKeys.INSTALLED)) {
    response.setStatus(301);
    response.setHeader("Location", "forums/list.page");
} else {
	response.setStatus(301);
	response.setHeader("Location", "install.jsp");	
}
%>
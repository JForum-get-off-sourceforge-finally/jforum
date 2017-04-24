package net.jforum.csrf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.log4j.Logger;

/**
 * Pass method instead of requestUri to match unprotected logic from csrf.properties
 * @author Jeanne Boyarsky
 */

public class CsrfHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger LOGGER = Logger.getLogger(CsrfHttpServletRequestWrapper.class);

    private final String actionMethodName;
	private HttpServletRequest originalRequest;

    public CsrfHttpServletRequestWrapper (final HttpServletRequest request, final String actionMethodName) {
        super(request);
        this.actionMethodName = actionMethodName;
        this.originalRequest = request;
    }

    @Override
    public String getRequestURI() {
        return actionMethodName;
    }

    @Override
	public String getHeader (String name) {
		if (name.toLowerCase().equals("x-requested-with")) {
			String value = originalRequest.getHeader("X-Requested-With");
			if (value == null) {
				return null;
			} else if (value.equals("XMLHttpRequest")) {
				return value;
			} else {
				// This is the change to fix non-standard browser behavior
				// E.g., the Android 4.1+ browser sends that header even for non-AJAX requests
				return null;
			}
		} 
		
		return originalRequest.getHeader(name);
	}
}

